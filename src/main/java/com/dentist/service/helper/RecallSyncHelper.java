package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.Recall;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudRecallRepositoryJPA;
import com.dentist.repository.local.LocalRecallRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for handling Recall entity synchronization between local and cloud databases.
 * 
 * This class encapsulates all recall-specific sync logic including:
 * - CREATE/UPDATE/DELETE operations
 * - Intelligent conflict resolution with field-level merging
 * - Smart merge capabilities for concurrent updates
 * - Account-based filtering for multi-tenant security
 * 
 * Extracted from EventBasedSyncServiceImpl to improve code organization and maintainability.
 */
@Component
public class RecallSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(RecallSyncHelper.class);

    private final CloudRecallRepositoryJPA cloudRecallRepository;
    private final LocalRecallRepositoryJPA localRecallRepository;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public RecallSyncHelper(CloudRecallRepositoryJPA cloudRecallRepository,
            LocalRecallRepositoryJPA localRecallRepository) {
        this.cloudRecallRepository = cloudRecallRepository;
        this.localRecallRepository = localRecallRepository;
    }

    /**
     * Processes a recall sync item based on the change type.
     */
    public void processRecallSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncRecallCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncRecallUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncRecallDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for recalls.
     */
    private void syncRecallCreate(UUID recallId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Recall> localRecallOpt = localRecallRepository.findByIdAndAccountId(recallId, accountId);
            
            if (localRecallOpt.isPresent()) {
                Recall localRecall = localRecallOpt.get();
                
                // Check if recall already exists in cloud
                Optional<Recall> cloudRecallOpt = cloudRecallRepository.findByIdAndAccountId(recallId, accountId);
                
                if (cloudRecallOpt.isEmpty()) {
                    // Create in cloud
                    localRecall.setNew(true);
                    cloudRecallRepository.save(localRecall);
                    logger.info("Created recall {} in cloud", recallId);
                } else {
                    // Already exists, treat as update
                    syncRecallUpdate(recallId, localToCloud);
                }
            } else {
                logger.warn("Local recall {} not found for CREATE sync", recallId);
            }
        } else {
            // Sync from cloud to local
            Optional<Recall> cloudRecallOpt = cloudRecallRepository.findByIdAndAccountId(recallId, accountId);
            
            if (cloudRecallOpt.isPresent()) {
                Recall cloudRecall = cloudRecallOpt.get();
                
                // Check if recall already exists locally
                Optional<Recall> localRecallOpt = localRecallRepository.findByIdAndAccountId(recallId, accountId);
                
                if (localRecallOpt.isEmpty()) {
                    // Create in local
                    cloudRecall.setNew(true);
                    localRecallRepository.save(cloudRecall);
                    logger.info("Created recall {} in local from cloud", recallId);
                } else {
                    // Already exists, treat as update
                    syncRecallUpdate(recallId, localToCloud);
                }
            } else {
                logger.warn("Cloud recall {} not found for CREATE sync for account {}", recallId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for recalls with intelligent conflict resolution.
     */
    private void syncRecallUpdate(UUID recallId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Recall> localRecallOpt = localRecallRepository.findByIdAndAccountId(recallId, accountId);
            
            if (localRecallOpt.isPresent()) {
                Recall localRecall = localRecallOpt.get();
                Optional<Recall> cloudRecallOpt = cloudRecallRepository.findByIdAndAccountId(recallId, accountId);
                
                if (cloudRecallOpt.isPresent()) {
                    Recall cloudRecall = cloudRecallOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Recall mergedRecall = (Recall) SyncUtil.performSimpleTimestampMerge(localRecall, cloudRecall, true);
                    
                    if (mergedRecall != null) {
                        // Changes were merged or local is newer
                        cloudRecallRepository.save(mergedRecall);
                        logger.info("Updated/merged recall {} in cloud", recallId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud recall {} is newer than local, no sync needed", recallId);
                    }
                } else {
                    // Cloud recall doesn't exist, create it
                    localRecall.setNew(true);
                    cloudRecallRepository.save(localRecall);
                    logger.info("Created recall {} in cloud (was UPDATE but not found)", recallId);
                }
            } else {
                logger.warn("Local recall {} not found for UPDATE sync", recallId);
            }
        } else {
            // Sync from cloud to local
            Optional<Recall> cloudRecallOpt = cloudRecallRepository.findByIdAndAccountId(recallId, accountId);
            
            if (cloudRecallOpt.isPresent()) {
                Recall cloudRecall = cloudRecallOpt.get();
                Optional<Recall> localRecallOpt = localRecallRepository.findByIdAndAccountId(recallId, accountId);
                
                if (localRecallOpt.isPresent()) {
                    Recall localRecall = localRecallOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Recall mergedRecall = (Recall) SyncUtil.performSimpleTimestampMerge(cloudRecall, localRecall, false);
                    
                    if (mergedRecall != null) {
                        // Changes were merged or cloud is newer
                        localRecallRepository.save(mergedRecall);
                        logger.info("Updated/merged recall {} in local from cloud", recallId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local recall {} is newer than cloud, no sync needed", recallId);
                    }
                } else {
                    // Local recall doesn't exist, create it
                    cloudRecall.setNew(true);
                    localRecallRepository.save(cloudRecall);
                    logger.info("Created recall {} in local from cloud (was UPDATE but not found)", recallId);
                }
            } else {
                logger.warn("Cloud recall {} not found for UPDATE sync for account {}", recallId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for recalls.
     */
    private void syncRecallDelete(UUID recallId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<Recall> cloudRecallOpt = cloudRecallRepository.findByIdAndAccountId(recallId, accountId);
            
            if (cloudRecallOpt.isPresent()) {
                Recall cloudRecall = cloudRecallOpt.get();
                cloudRecall.setStatus(Recall.STATUS_CANCELLED);
                cloudRecallRepository.save(cloudRecall);
                logger.info("Marked recall {} as deleted in cloud", recallId);
            } else {
                logger.info("Recall {} not found in cloud for DELETE sync (already deleted?)", recallId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<Recall> localRecallOpt = localRecallRepository.findByIdAndAccountId(recallId, accountId);
            
            if (localRecallOpt.isPresent()) {
                Recall localRecall = localRecallOpt.get();
                localRecall.setStatus(Recall.STATUS_CANCELLED);
                localRecallRepository.save(localRecall);
                logger.info("Marked recall {} as deleted in local from cloud", recallId);
            } else {
                logger.info("Recall {} not found in local for DELETE sync (already deleted?)", recallId);
            }
        }
    }
}
