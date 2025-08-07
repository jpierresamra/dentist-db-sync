package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.Operation;
import com.dentist.beans.SyncQueueItem;
import com.dentist.beans.Tooth;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudOperationRepositoryJPA;
import com.dentist.repository.cloud.CloudToothRepositoryJPA;
import com.dentist.repository.local.LocalOperationRepositoryJPA;
import com.dentist.repository.local.LocalToothRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for Operation entity synchronization between local and cloud databases.
 * Provides smart merge capabilities with field-level conflict resolution and account-based filtering.
 * 
 * Features:
 * - Account-based multi-tenant security
 * - Smart merge with comprehensive field handling
 * - Operation-specific logic (status, amount, dental procedures)
 * - Enhanced logging and error handling
 * 
 * @author Generated Helper Pattern
 * @version 1.0
 */
@Component
public class OperationSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(OperationSyncHelper.class);
    
    private final CloudOperationRepositoryJPA cloudOperationRepository;
    private final LocalOperationRepositoryJPA localOperationRepository;
    private final LocalToothRepositoryJPA localToothRepository;
    private final CloudToothRepositoryJPA cloudToothRepository;
    
    
    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public OperationSyncHelper(
            CloudOperationRepositoryJPA cloudOperationRepository,
            LocalOperationRepositoryJPA localOperationRepository,
            LocalToothRepositoryJPA localToothRepository,
            CloudToothRepositoryJPA cloudToothRepository) {
        this.cloudOperationRepository = cloudOperationRepository;
        this.localOperationRepository = localOperationRepository;
        this.localToothRepository = localToothRepository;
        this.cloudToothRepository = cloudToothRepository;
    }

    /**
     * Process an operation sync item with smart merge capabilities
     * @param syncItem the sync queue item
     * @param localToCloud true if syncing from local to cloud, false if cloud to local
     * @throws Exception if sync fails
     */
    public void processOperationSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncOperationCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncOperationUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncOperationDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for operations.
     */
    private void syncOperationCreate(UUID operationId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Operation> localOperationOpt = localOperationRepository.findByIdAndAccountId(operationId, accountId);
            
            if (localOperationOpt.isPresent()) {
                Operation localOperation = localOperationOpt.get();
                
                // Check if operation already exists in cloud
                Optional<Operation> cloudOperationOpt = cloudOperationRepository.findByIdAndAccountId(operationId, accountId);
                
                if (cloudOperationOpt.isEmpty()) {
                    // Create in cloud
                    // Set managed teeth from local repository for each operation tooth
                    if (localOperation.getOperationTeeth() != null) {
                        for (var operationTooth : localOperation.getOperationTeeth()) {
                            if (operationTooth.getTooth() != null) {
                                Optional<Tooth> managedTooth = localToothRepository.findById(operationTooth.getTooth().getId());
                                if (managedTooth.isPresent()) {
                                    operationTooth.setTooth(managedTooth.get());
                                }
                            }
                        }
                    }
                    
                    //localOperation.setNew(true);
                    cloudOperationRepository.save(localOperation);
                    logger.info("Created operation {} in cloud", operationId);
                } else {
                    // Already exists, treat as update
                    syncOperationUpdate(operationId, localToCloud);
                }
            } else {
                logger.warn("Local operation {} not found for CREATE sync", operationId);
            }
        } else {
            // Sync from cloud to local
            Optional<Operation> cloudOperationOpt = cloudOperationRepository.findByIdAndAccountId(operationId, accountId);
            
            if (cloudOperationOpt.isPresent()) {
                Operation cloudOperation = cloudOperationOpt.get();
                
                // Check if operation already exists locally
                Optional<Operation> localOperationOpt = localOperationRepository.findByIdAndAccountId(operationId, accountId);
                
                if (localOperationOpt.isEmpty()) {
                    // Create in local
                    localOperationRepository.save(cloudOperation);
                    logger.info("Created operation {} in local from cloud", operationId);
                } else {
                    // Already exists, treat as update
                    syncOperationUpdate(operationId, localToCloud);
                }
            } else {
                logger.warn("Cloud operation {} not found for CREATE sync for account {}", operationId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for operations with intelligent conflict resolution.
     */
    private void syncOperationUpdate(UUID operationId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Operation> localOperationOpt = localOperationRepository.findByIdAndAccountId(operationId, accountId);
            
            if (localOperationOpt.isPresent()) {
                Operation localOperation = localOperationOpt.get();
                Optional<Operation> cloudOperationOpt = cloudOperationRepository.findByIdAndAccountId(operationId, accountId);
                
                if (cloudOperationOpt.isPresent()) {
                    Operation cloudOperation = cloudOperationOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Operation mergedOperation = (Operation) SyncUtil.performSimpleTimestampMerge(localOperation, cloudOperation, true);
                    
                    if (mergedOperation != null) {
                        
                        // Load the existing cloud operation to enable proper cascade orphan removal
                        Operation existingCloudOperation = cloudOperationOpt.get();
                        
                        // Clear the existing collection to trigger orphan removal
                        existingCloudOperation.getOperationTeeth().clear();
                        
                        // Copy the new collection from merged operation
                        if (mergedOperation.getOperationTeeth() != null) {
                            existingCloudOperation.getOperationTeeth().addAll(mergedOperation.getOperationTeeth());
                        }
                        
                        // Copy other fields from merged operation
                        existingCloudOperation.setDescription(mergedOperation.getDescription());
                        existingCloudOperation.setFee(mergedOperation.getFee());
                        existingCloudOperation.setOperateDate(mergedOperation.getOperateDate());
                        existingCloudOperation.setStatus(mergedOperation.getStatus());
                        existingCloudOperation.setUpdateDate(mergedOperation.getUpdateDate());
                        
                        cloudOperationRepository.save(existingCloudOperation);
                        logger.info("Updated/merged operation {} in cloud", operationId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud operation {} is newer than local, no sync needed", operationId);
                    }
                } else {
                    cloudOperationRepository.save(localOperation);
                    logger.info("Created operation {} in cloud (was UPDATE but not found)", operationId);
                }
            } else {
                logger.warn("Local operation {} not found for UPDATE sync", operationId);
            }
        } else {
            // Sync from cloud to local
            Optional<Operation> cloudOperationOpt = cloudOperationRepository.findByIdAndAccountId(operationId, accountId);
            
            if (cloudOperationOpt.isPresent()) {
                Operation cloudOperation = cloudOperationOpt.get();
                Optional<Operation> localOperationOpt = localOperationRepository.findByIdAndAccountId(operationId, accountId);
                
                if (localOperationOpt.isPresent()) {
                    Operation localOperation = localOperationOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Operation mergedOperation = (Operation) SyncUtil.performSimpleTimestampMerge(cloudOperation, localOperation, false);
                    
                    if (mergedOperation != null) {

                    	// Load the existing local operation to enable proper cascade orphan removal
                        Operation existingLocalOperation = localOperationOpt.get();
                        
                        // Clear the existing collection to trigger orphan removal
                        existingLocalOperation.getOperationTeeth().clear();
                        
                        // Copy the new collection from merged operation
                        if (mergedOperation.getOperationTeeth() != null) {
                            existingLocalOperation.getOperationTeeth().addAll(mergedOperation.getOperationTeeth());
                        }
                        
                        // Copy other fields from merged operation
                        existingLocalOperation.setDescription(mergedOperation.getDescription());
                        existingLocalOperation.setFee(mergedOperation.getFee());
                        existingLocalOperation.setOperateDate(mergedOperation.getOperateDate());
                        existingLocalOperation.setStatus(mergedOperation.getStatus());
                        existingLocalOperation.setUpdateDate(mergedOperation.getUpdateDate());
                        
                        localOperationRepository.save(existingLocalOperation);
                        logger.info("Updated/merged operation {} in local from cloud", operationId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local operation {} is newer than cloud, no sync needed", operationId);
                    }
                } else {
                    // Local operation doesn't exist, create it
                    cloudOperation.setNew(true);
                    localOperationRepository.save(cloudOperation);
                    logger.info("Created operation {} in local from cloud (was UPDATE but not found)", operationId);
                }
            } else {
                logger.warn("Cloud operation {} not found for UPDATE sync for account {}", operationId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for operations.
     */
    private void syncOperationDelete(UUID operationId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<Operation> cloudOperationOpt = cloudOperationRepository.findByIdAndAccountId(operationId, accountId);
            
            if (cloudOperationOpt.isPresent()) {
                Operation cloudOperation = cloudOperationOpt.get();
                cloudOperation.setStatus(Operation.STATUS_DELETED);
                cloudOperationRepository.save(cloudOperation);
                logger.info("Marked operation {} as deleted in cloud", operationId);
            } else {
                logger.info("Operation {} not found in cloud for DELETE sync (already deleted?)", operationId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<Operation> localOperationOpt = localOperationRepository.findByIdAndAccountId(operationId, accountId);
            
            if (localOperationOpt.isPresent()) {
                Operation localOperation = localOperationOpt.get();
                localOperation.setStatus(Operation.STATUS_DELETED);
                localOperationRepository.save(localOperation);
                logger.info("Marked operation {} as deleted in local from cloud", operationId);
            } else {
                logger.info("Operation {} not found in local for DELETE sync (already deleted?)", operationId);
            }
        }
    }
}
