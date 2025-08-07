package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.Procedure;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudProcedureRepositoryJPA;
import com.dentist.repository.local.LocalProcedureRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for handling Procedure entity synchronization between local and cloud databases.
 * 
 * This class encapsulates all procedure-specific sync logic including:
 * - CREATE/UPDATE/DELETE operations
 * - Intelligent conflict resolution with field-level merging
 * - Smart merge capabilities for concurrent updates
 * - Account-based filtering for multi-tenant security
 * 
 * Extracted from EventBasedSyncServiceImpl to improve code organization and maintainability.
 */
@Component
public class ProcedureSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(ProcedureSyncHelper.class);

    private final CloudProcedureRepositoryJPA cloudProcedureRepository;
    private final LocalProcedureRepositoryJPA localProcedureRepository;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public ProcedureSyncHelper(CloudProcedureRepositoryJPA cloudProcedureRepository,
                            LocalProcedureRepositoryJPA localProcedureRepository) {
        this.cloudProcedureRepository = cloudProcedureRepository;
        this.localProcedureRepository = localProcedureRepository;
    }

    /**
     * Processes a procedure sync item based on the change type.
     */
    public void processProcedureSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncProcedureCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncProcedureUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncProcedureDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for procedures.
     */
    private void syncProcedureCreate(UUID procedureId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Procedure> localProcedureOpt = localProcedureRepository.findByIdAndAccountId(procedureId, accountId);
            
            if (localProcedureOpt.isPresent()) {
                Procedure localProcedure = localProcedureOpt.get();
                
                // Check if procedure already exists in cloud
                Optional<Procedure> cloudProcedureOpt = cloudProcedureRepository.findByIdAndAccountId(procedureId, accountId);
                
                if (cloudProcedureOpt.isEmpty()) {
                    // Create in cloud
                    localProcedure.setNew(true);
                    cloudProcedureRepository.save(localProcedure);
                    logger.info("Created procedure {} in cloud", procedureId);
                } else {
                    // Already exists, treat as update
                    syncProcedureUpdate(procedureId, localToCloud);
                }
            } else {
                logger.warn("Local procedure {} not found for CREATE sync", procedureId);
            }
        } else {
            // Sync from cloud to local
            Optional<Procedure> cloudProcedureOpt = cloudProcedureRepository.findByIdAndAccountId(procedureId, accountId);
            
            if (cloudProcedureOpt.isPresent()) {
                Procedure cloudProcedure = cloudProcedureOpt.get();
                
                // Check if procedure already exists locally
                Optional<Procedure> localProcedureOpt = localProcedureRepository.findByIdAndAccountId(procedureId, accountId);
                
                if (localProcedureOpt.isEmpty()) {
                    // Create in local
                    cloudProcedure.setNew(true);
                    localProcedureRepository.save(cloudProcedure);
                    logger.info("Created procedure {} in local from cloud", procedureId);
                } else {
                    // Already exists, treat as update
                    syncProcedureUpdate(procedureId, localToCloud);
                }
            } else {
                logger.warn("Cloud procedure {} not found for CREATE sync for account {}", procedureId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for procedures with intelligent conflict resolution.
     */
    private void syncProcedureUpdate(UUID procedureId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Procedure> localProcedureOpt = localProcedureRepository.findByIdAndAccountId(procedureId, accountId);
            
            if (localProcedureOpt.isPresent()) {
                Procedure localProcedure = localProcedureOpt.get();
                Optional<Procedure> cloudProcedureOpt = cloudProcedureRepository.findByIdAndAccountId(procedureId, accountId);
                
                if (cloudProcedureOpt.isPresent()) {
                    Procedure cloudProcedure = cloudProcedureOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Procedure mergedProcedure = (Procedure) SyncUtil.performSimpleTimestampMerge(localProcedure, cloudProcedure, true);
                    
                    if (mergedProcedure != null) {
                        // Changes were merged or local is newer
                        cloudProcedureRepository.save(mergedProcedure);
                        logger.info("Updated/merged procedure {} in cloud", procedureId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud procedure {} is newer than local, no sync needed", procedureId);
                    }
                } else {
                    // Cloud procedure doesn't exist, create it
                    localProcedure.setNew(true);
                    cloudProcedureRepository.save(localProcedure);
                    logger.info("Created procedure {} in cloud (was UPDATE but not found)", procedureId);
                }
            } else {
                logger.warn("Local procedure {} not found for UPDATE sync", procedureId);
            }
        } else {
            // Sync from cloud to local
            Optional<Procedure> cloudProcedureOpt = cloudProcedureRepository.findByIdAndAccountId(procedureId, accountId);
            
            if (cloudProcedureOpt.isPresent()) {
                Procedure cloudProcedure = cloudProcedureOpt.get();
                Optional<Procedure> localProcedureOpt = localProcedureRepository.findByIdAndAccountId(procedureId, accountId);
                
                if (localProcedureOpt.isPresent()) {
                    Procedure localProcedure = localProcedureOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Procedure mergedProcedure = (Procedure) SyncUtil.performSimpleTimestampMerge(cloudProcedure, localProcedure, false);
                    
                    if (mergedProcedure != null) {
                        // Changes were merged or cloud is newer
                        localProcedureRepository.save(mergedProcedure);
                        logger.info("Updated/merged procedure {} in local from cloud", procedureId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local procedure {} is newer than cloud, no sync needed", procedureId);
                    }
                } else {
                    // Local procedure doesn't exist, create it
                    cloudProcedure.setNew(true);
                    localProcedureRepository.save(cloudProcedure);
                    logger.info("Created procedure {} in local from cloud (was UPDATE but not found)", procedureId);
                }
            } else {
                logger.warn("Cloud procedure {} not found for UPDATE sync for account {}", procedureId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for procedures.
     */
    private void syncProcedureDelete(UUID procedureId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<Procedure> cloudProcedureOpt = cloudProcedureRepository.findByIdAndAccountId(procedureId, accountId);
            
            if (cloudProcedureOpt.isPresent()) {
                Procedure cloudProcedure = cloudProcedureOpt.get();
                cloudProcedure.setStatus(Procedure.STATUS_DELETED);
                cloudProcedureRepository.save(cloudProcedure);
                logger.info("Marked procedure {} as deleted in cloud", procedureId);
            } else {
                logger.info("Procedure {} not found in cloud for DELETE sync (already deleted?)", procedureId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<Procedure> localProcedureOpt = localProcedureRepository.findByIdAndAccountId(procedureId, accountId);
            
            if (localProcedureOpt.isPresent()) {
                Procedure localProcedure = localProcedureOpt.get();
                localProcedure.setStatus(Procedure.STATUS_DELETED);
                localProcedureRepository.save(localProcedure);
                logger.info("Marked procedure {} as deleted in local from cloud", procedureId);
            } else {
                logger.info("Procedure {} not found in local for DELETE sync (already deleted?)", procedureId);
            }
        }
    }
}
