package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.SyncQueueItem;
import com.dentist.beans.Treatment;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudTreatmentRepositoryJPA;
import com.dentist.repository.local.LocalTreatmentRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for Treatment entity synchronization between local and cloud databases.
 * Provides smart merge capabilities with field-level conflict resolution and account-based filtering.
 * 
 * Features:
 * - Account-based multi-tenant security
 * - Smart merge with comprehensive field handling
 * - Treatment-specific logic (procedure, cost, dental notes)
 * - Enhanced logging and error handling
 * 
 * @author Generated Helper Pattern
 * @version 1.0
 */
@Component
public class TreatmentSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(TreatmentSyncHelper.class);
    
    private final CloudTreatmentRepositoryJPA cloudTreatmentRepository;
    private final LocalTreatmentRepositoryJPA localTreatmentRepository;
    
    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public TreatmentSyncHelper(
            CloudTreatmentRepositoryJPA cloudTreatmentRepository,
            LocalTreatmentRepositoryJPA localTreatmentRepository) {
        this.cloudTreatmentRepository = cloudTreatmentRepository;
        this.localTreatmentRepository = localTreatmentRepository;
    }

    /**
     * Process a treatment sync item with smart merge capabilities
     * @param syncItem the sync queue item
     * @param localToCloud true if syncing from local to cloud, false if cloud to local
     * @throws Exception if sync fails
     */
    public void processTreatmentSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncTreatmentCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncTreatmentUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncTreatmentDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for treatments.
     */
    private void syncTreatmentCreate(UUID treatmentId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Treatment> localTreatmentOpt = localTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
            
            if (localTreatmentOpt.isPresent()) {
                Treatment localTreatment = localTreatmentOpt.get();
                
                // Check if treatment already exists in cloud
                Optional<Treatment> cloudTreatmentOpt = cloudTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
                
                if (cloudTreatmentOpt.isEmpty()) {
                    // Create in cloud
                    cloudTreatmentRepository.save(localTreatment);
                    logger.info("Created treatment {} in cloud", treatmentId);
                } else {
                    // Already exists, treat as update
                    syncTreatmentUpdate(treatmentId, localToCloud);
                }
            } else {
                logger.warn("Local treatment {} not found for CREATE sync", treatmentId);
            }
        } else {
            // Sync from cloud to local
            Optional<Treatment> cloudTreatmentOpt = cloudTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
            
            if (cloudTreatmentOpt.isPresent()) {
                Treatment cloudTreatment = cloudTreatmentOpt.get();
                
                // Check if treatment already exists locally
                Optional<Treatment> localTreatmentOpt = localTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
                
                if (localTreatmentOpt.isEmpty()) {
                    // Create in local
                    localTreatmentRepository.save(cloudTreatment);
                    logger.info("Created treatment {} in local from cloud", treatmentId);
                } else {
                    // Already exists, treat as update
                    syncTreatmentUpdate(treatmentId, localToCloud);
                }
            } else {
                logger.warn("Cloud treatment {} not found for CREATE sync for account {}", treatmentId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for treatments with intelligent conflict resolution.
     */
    private void syncTreatmentUpdate(UUID treatmentId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Treatment> localTreatmentOpt = localTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
            
            if (localTreatmentOpt.isPresent()) {
                Treatment localTreatment = localTreatmentOpt.get();
                Optional<Treatment> cloudTreatmentOpt = cloudTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
                
                if (cloudTreatmentOpt.isPresent()) {
                    Treatment cloudTreatment = cloudTreatmentOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Treatment mergedTreatment = (Treatment) SyncUtil.performSimpleTimestampMerge(localTreatment, cloudTreatment, true);
                    
                    if (mergedTreatment != null) {
                        // Load the existing cloud treatment to enable proper cascade orphan removal
                        Treatment existingCloudTreatment = cloudTreatmentOpt.get();
                        
                        // Clear the existing collection to trigger orphan removal
                        existingCloudTreatment.getTreatmentTeeth().clear();
                        
                        // Copy the new collection from merged treatment
                        if (mergedTreatment.getTreatmentTeeth() != null) {
                            existingCloudTreatment.getTreatmentTeeth().addAll(mergedTreatment.getTreatmentTeeth());
                        }
                        
                        // Copy other fields from merged treatment
                        existingCloudTreatment.setDescription(mergedTreatment.getDescription());
                        existingCloudTreatment.setOperationId(mergedTreatment.getOperationId());
                        existingCloudTreatment.setFee(mergedTreatment.getFee());
                        existingCloudTreatment.setOperateDate(mergedTreatment.getOperateDate());
                        existingCloudTreatment.setStatus(mergedTreatment.getStatus());
                        existingCloudTreatment.setUpdateDate(mergedTreatment.getUpdateDate());
                        
                        // Save the managed entity (this will trigger orphan removal)
                        cloudTreatmentRepository.save(existingCloudTreatment);
                        logger.info("Updated/merged treatment {} in cloud", treatmentId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud treatment {} is newer than local, no sync needed", treatmentId);
                    }
                } else {
                    // Cloud treatment doesn't exist, create it
                    localTreatment.setNew(true);
                    cloudTreatmentRepository.save(localTreatment);
                    logger.info("Created treatment {} in cloud (was UPDATE but not found)", treatmentId);
                }
            } else {
                logger.warn("Local treatment {} not found for UPDATE sync", treatmentId);
            }
        } else {
            // Sync from cloud to local
            Optional<Treatment> cloudTreatmentOpt = cloudTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
            
            if (cloudTreatmentOpt.isPresent()) {
                Treatment cloudTreatment = cloudTreatmentOpt.get();
                Optional<Treatment> localTreatmentOpt = localTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
                
                if (localTreatmentOpt.isPresent()) {
                    Treatment localTreatment = localTreatmentOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Treatment mergedTreatment = (Treatment) SyncUtil.performSimpleTimestampMerge(cloudTreatment, localTreatment, false);
                    
                    if (mergedTreatment != null) {
                        // Changes were merged or cloud is newer
                    	
                    	// Load the existing local treatment to enable proper cascade orphan removal
                        Treatment existingLocalTreatment = localTreatmentOpt.get();
                        
                        // Clear the existing collection to trigger orphan removal
                        existingLocalTreatment.getTreatmentTeeth().clear();
                        
                        // Copy the new collection from merged treatment
                        if (mergedTreatment.getTreatmentTeeth() != null) {
                            existingLocalTreatment.getTreatmentTeeth().addAll(mergedTreatment.getTreatmentTeeth());
                        }
                        
                        // Copy other fields from merged treatment
                        existingLocalTreatment.setDescription(mergedTreatment.getDescription());
                        existingLocalTreatment.setFee(mergedTreatment.getFee());
                        existingLocalTreatment.setOperateDate(mergedTreatment.getOperateDate());
                        existingLocalTreatment.setOperationId(mergedTreatment.getOperationId());
                        existingLocalTreatment.setStatus(mergedTreatment.getStatus());
                        existingLocalTreatment.setUpdateDate(mergedTreatment.getUpdateDate());
                        
                        // Save the managed entity (this will trigger orphan removal)
                        localTreatmentRepository.save(existingLocalTreatment);
                    	
                        logger.info("Updated/merged treatment {} in local from cloud", treatmentId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local treatment {} is newer than cloud, no sync needed", treatmentId);
                    }
                } else {
                    // Local treatment doesn't exist, create it
                    cloudTreatment.setNew(true);
                    localTreatmentRepository.save(cloudTreatment);
                    logger.info("Created treatment {} in local from cloud (was UPDATE but not found)", treatmentId);
                }
            } else {
                logger.warn("Cloud treatment {} not found for UPDATE sync for account {}", treatmentId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for treatments.
     */
    private void syncTreatmentDelete(UUID treatmentId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<Treatment> cloudTreatmentOpt = cloudTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
            
            if (cloudTreatmentOpt.isPresent()) {
                Treatment cloudTreatment = cloudTreatmentOpt.get();
                cloudTreatment.setStatus(Treatment.STATUS_DELETED);
                cloudTreatmentRepository.save(cloudTreatment);
                logger.info("Marked treatment {} as deleted in cloud", treatmentId);
            } else {
                logger.info("Treatment {} not found in cloud for DELETE sync (already deleted?)", treatmentId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<Treatment> localTreatmentOpt = localTreatmentRepository.findByIdAndAccountId(treatmentId, accountId);
            
            if (localTreatmentOpt.isPresent()) {
                Treatment localTreatment = localTreatmentOpt.get();
                localTreatment.setStatus(Treatment.STATUS_DELETED);
                localTreatmentRepository.save(localTreatment);
                logger.info("Marked treatment {} as deleted in local from cloud", treatmentId);
            } else {
                logger.info("Treatment {} not found in local for DELETE sync (already deleted?)", treatmentId);
            }
        }
    }
}
