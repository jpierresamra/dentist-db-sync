package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.Clinic;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudClinicRepositoryJPA;
import com.dentist.repository.local.LocalClinicRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for handling Clinic entity synchronization between local and cloud databases.
 * 
 * This class encapsulates all clinic-specific sync logic including:
 * - CREATE/UPDATE/DELETE operations
 * - Intelligent conflict resolution with field-level merging
 * - Smart merge capabilities for concurrent updates
 * - Account-based filtering for multi-tenant security
 * 
 * Extracted from EventBasedSyncServiceImpl to improve code organization and maintainability.
 */
@Component
public class ClinicSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(ClinicSyncHelper.class);

    private final CloudClinicRepositoryJPA cloudClinicRepository;
    private final LocalClinicRepositoryJPA localClinicRepository;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public ClinicSyncHelper(CloudClinicRepositoryJPA cloudClinicRepository,
                            LocalClinicRepositoryJPA localClinicRepository) {
        this.cloudClinicRepository = cloudClinicRepository;
        this.localClinicRepository = localClinicRepository;
    }

    /**
     * Processes a clinic sync item based on the change type.
     */
    public void processClinicSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncClinicCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncClinicUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncClinicDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for clinics.
     */
    private void syncClinicCreate(UUID clinicId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Clinic> localClinicOpt = localClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
            
            if (localClinicOpt.isPresent()) {
                Clinic localClinic = localClinicOpt.get();
                
                // Check if clinic already exists in cloud
                Optional<Clinic> cloudClinicOpt = cloudClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
                
                if (cloudClinicOpt.isEmpty()) {
                    // Create in cloud
                    localClinic.setNew(true);
                    cloudClinicRepository.save(localClinic);
                    logger.info("Created clinic {} in cloud", clinicId);
                } else {
                    // Already exists, treat as update
                    syncClinicUpdate(clinicId, localToCloud);
                }
            } else {
                logger.warn("Local clinic {} not found for CREATE sync", clinicId);
            }
        } else {
            // Sync from cloud to local
            Optional<Clinic> cloudClinicOpt = cloudClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
            
            if (cloudClinicOpt.isPresent()) {
                Clinic cloudClinic = cloudClinicOpt.get();
                
                // Check if clinic already exists locally
                Optional<Clinic> localClinicOpt = localClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
                
                if (localClinicOpt.isEmpty()) {
                    // Create in local
                    cloudClinic.setNew(true);
                    localClinicRepository.save(cloudClinic);
                    logger.info("Created clinic {} in local from cloud", clinicId);
                } else {
                    // Already exists, treat as update
                    syncClinicUpdate(clinicId, localToCloud);
                }
            } else {
                logger.warn("Cloud clinic {} not found for CREATE sync for account {}", clinicId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for clinics with intelligent conflict resolution.
     */
    private void syncClinicUpdate(UUID clinicId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Clinic> localClinicOpt = localClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
            
            if (localClinicOpt.isPresent()) {
                Clinic localClinic = localClinicOpt.get();
                Optional<Clinic> cloudClinicOpt = cloudClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
                
                if (cloudClinicOpt.isPresent()) {
                    Clinic cloudClinic = cloudClinicOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Clinic mergedClinic = (Clinic) SyncUtil.performSimpleTimestampMerge(localClinic, cloudClinic, true);
                    
                    if (mergedClinic != null) {
                        // Changes were merged or local is newer
                        cloudClinicRepository.save(mergedClinic);
                        logger.info("Updated/merged clinic {} in cloud", clinicId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud clinic {} is newer than local, no sync needed", clinicId);
                    }
                } else {
                    // Cloud clinic doesn't exist, create it
                    localClinic.setNew(true);
                    cloudClinicRepository.save(localClinic);
                    logger.info("Created clinic {} in cloud (was UPDATE but not found)", clinicId);
                }
            } else {
                logger.warn("Local clinic {} not found for UPDATE sync", clinicId);
            }
        } else {
            // Sync from cloud to local
            Optional<Clinic> cloudClinicOpt = cloudClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
            
            if (cloudClinicOpt.isPresent()) {
                Clinic cloudClinic = cloudClinicOpt.get();
                Optional<Clinic> localClinicOpt = localClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
                
                if (localClinicOpt.isPresent()) {
                    Clinic localClinic = localClinicOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Clinic mergedClinic = (Clinic) SyncUtil.performSimpleTimestampMerge(cloudClinic, localClinic, false);
                    
                    if (mergedClinic != null) {
                        // Changes were merged or cloud is newer
                        localClinicRepository.save(mergedClinic);
                        logger.info("Updated/merged clinic {} in local from cloud", clinicId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local clinic {} is newer than cloud, no sync needed", clinicId);
                    }
                } else {
                    // Local clinic doesn't exist, create it
                    cloudClinic.setNew(true);
                    localClinicRepository.save(cloudClinic);
                    logger.info("Created clinic {} in local from cloud (was UPDATE but not found)", clinicId);
                }
            } else {
                logger.warn("Cloud clinic {} not found for UPDATE sync for account {}", clinicId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for clinics.
     */
    private void syncClinicDelete(UUID clinicId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<Clinic> cloudClinicOpt = cloudClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
            
            if (cloudClinicOpt.isPresent()) {
                Clinic cloudClinic = cloudClinicOpt.get();
                cloudClinic.setStatus(Clinic.STATUS_DELETED);
                cloudClinicRepository.save(cloudClinic);
                logger.info("Marked clinic {} as deleted in cloud", clinicId);
            } else {
                logger.info("Clinic {} not found in cloud for DELETE sync (already deleted?)", clinicId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<Clinic> localClinicOpt = localClinicRepository.findByClinicIdAndAccountId(clinicId, accountId);
            
            if (localClinicOpt.isPresent()) {
                Clinic localClinic = localClinicOpt.get();
                localClinic.setStatus(Clinic.STATUS_DELETED);
                localClinicRepository.save(localClinic);
                logger.info("Marked clinic {} as deleted in local from cloud", clinicId);
            } else {
                logger.info("Clinic {} not found in local for DELETE sync (already deleted?)", clinicId);
            }
        }
    }
}
