package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.ClassType;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudClassTypeRepositoryJPA;
import com.dentist.repository.local.LocalClassTypeRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for handling ClassType entity synchronization between local and cloud databases.
 * 
 * This class encapsulates all classtype-specific sync logic including:
 * - CREATE/UPDATE/DELETE operations
 * - Intelligent conflict resolution with field-level merging
 * - Smart merge capabilities for concurrent updates
 * - Account-based filtering for multi-tenant security
 * 
 * Extracted from EventBasedSyncServiceImpl to improve code organization and maintainability.
 */
@Component
public class ClassTypeSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(ClassTypeSyncHelper.class);

    private final CloudClassTypeRepositoryJPA cloudClassTypeRepository;
    private final LocalClassTypeRepositoryJPA localClassTypeRepository;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public ClassTypeSyncHelper(CloudClassTypeRepositoryJPA cloudClassTypeRepository,
                            LocalClassTypeRepositoryJPA localClassTypeRepository) {
        this.cloudClassTypeRepository = cloudClassTypeRepository;
        this.localClassTypeRepository = localClassTypeRepository;
    }

    /**
     * Processes a classtype sync item based on the change type.
     */
    public void processClassTypeSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncClassTypeCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncClassTypeUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncClassTypeDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for classtypes.
     */
    private void syncClassTypeCreate(UUID classTypeId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<ClassType> localClassTypeOpt = localClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
            
            if (localClassTypeOpt.isPresent()) {
                ClassType localClassType = localClassTypeOpt.get();
                
                // Check if classtype already exists in cloud
                Optional<ClassType> cloudClassTypeOpt = cloudClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
                
                if (cloudClassTypeOpt.isEmpty()) {
                    // Create in cloud
                    localClassType.setNew(true);
                    cloudClassTypeRepository.save(localClassType);
                    logger.info("Created classtype {} in cloud", classTypeId);
                } else {
                    // Already exists, treat as update
                    syncClassTypeUpdate(classTypeId, localToCloud);
                }
            } else {
                logger.warn("Local classtype {} not found for CREATE sync", classTypeId);
            }
        } else {
            // Sync from cloud to local
            Optional<ClassType> cloudClassTypeOpt = cloudClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
            
            if (cloudClassTypeOpt.isPresent()) {
                ClassType cloudClassType = cloudClassTypeOpt.get();
                
                // Check if classtype already exists locally
                Optional<ClassType> localClassTypeOpt = localClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
                
                if (localClassTypeOpt.isEmpty()) {
                    // Create in local
                    cloudClassType.setNew(true);
                    localClassTypeRepository.save(cloudClassType);
                    logger.info("Created classtype {} in local from cloud", classTypeId);
                } else {
                    // Already exists, treat as update
                    syncClassTypeUpdate(classTypeId, localToCloud);
                }
            } else {
                logger.warn("Cloud classtype {} not found for CREATE sync for account {}", classTypeId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for classtypes with intelligent conflict resolution.
     */
    private void syncClassTypeUpdate(UUID classTypeId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<ClassType> localClassTypeOpt = localClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
            
            if (localClassTypeOpt.isPresent()) {
                ClassType localClassType = localClassTypeOpt.get();
                Optional<ClassType> cloudClassTypeOpt = cloudClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
                
                if (cloudClassTypeOpt.isPresent()) {
                    ClassType cloudClassType = cloudClassTypeOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    ClassType mergedClassType = (ClassType) SyncUtil.performSimpleTimestampMerge(localClassType, cloudClassType, true);
                    
                    if (mergedClassType != null) {
                        // Changes were merged or local is newer
                        cloudClassTypeRepository.save(mergedClassType);
                        logger.info("Updated/merged classtype {} in cloud", classTypeId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud classtype {} is newer than local, no sync needed", classTypeId);
                    }
                } else {
                    // Cloud classtype doesn't exist, create it
                    localClassType.setNew(true);
                    cloudClassTypeRepository.save(localClassType);
                    logger.info("Created classtype {} in cloud (was UPDATE but not found)", classTypeId);
                }
            } else {
                logger.warn("Local classtype {} not found for UPDATE sync", classTypeId);
            }
        } else {
            // Sync from cloud to local
            Optional<ClassType> cloudClassTypeOpt = cloudClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
            
            if (cloudClassTypeOpt.isPresent()) {
                ClassType cloudClassType = cloudClassTypeOpt.get();
                Optional<ClassType> localClassTypeOpt = localClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
                
                if (localClassTypeOpt.isPresent()) {
                    ClassType localClassType = localClassTypeOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    ClassType mergedClassType = (ClassType) SyncUtil.performSimpleTimestampMerge(cloudClassType, localClassType, false);
                    
                    if (mergedClassType != null) {
                        // Changes were merged or cloud is newer
                        localClassTypeRepository.save(mergedClassType);
                        logger.info("Updated/merged classtype {} in local from cloud", classTypeId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local classtype {} is newer than cloud, no sync needed", classTypeId);
                    }
                } else {
                    // Local classtype doesn't exist, create it
                    cloudClassType.setNew(true);
                    localClassTypeRepository.save(cloudClassType);
                    logger.info("Created classtype {} in local from cloud (was UPDATE but not found)", classTypeId);
                }
            } else {
                logger.warn("Cloud classtype {} not found for UPDATE sync for account {}", classTypeId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for classtypes.
     */
    private void syncClassTypeDelete(UUID classTypeId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<ClassType> cloudClassTypeOpt = cloudClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
            
            if (cloudClassTypeOpt.isPresent()) {
                ClassType cloudClassType = cloudClassTypeOpt.get();
                cloudClassType.setStatus(ClassType.STATUS_DELETED);
                cloudClassTypeRepository.save(cloudClassType);
                logger.info("Marked classtype {} as deleted in cloud", classTypeId);
            } else {
                logger.info("ClassType {} not found in cloud for DELETE sync (already deleted?)", classTypeId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<ClassType> localClassTypeOpt = localClassTypeRepository.findByIdAndAccountId(classTypeId, accountId);
            
            if (localClassTypeOpt.isPresent()) {
                ClassType localClassType = localClassTypeOpt.get();
                localClassType.setStatus(ClassType.STATUS_DELETED);
                localClassTypeRepository.save(localClassType);
                logger.info("Marked classtype {} as deleted in local from cloud", classTypeId);
            } else {
                logger.info("ClassType {} not found in local for DELETE sync (already deleted?)", classTypeId);
            }
        }
    }
}
