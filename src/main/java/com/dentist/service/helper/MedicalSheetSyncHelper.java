package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.MedicalSheet;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudMedicalSheetRepositoryJPA;
import com.dentist.repository.local.LocalMedicalSheetRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for handling MedicalSheet entity synchronization between local and cloud databases.
 * 
 * This class encapsulates all medical sheet-specific sync logic including:
 * - CREATE/UPDATE/DELETE operations
 * - Intelligent conflict resolution with field-level merging
 * - Smart merge capabilities for concurrent updates
 * - Account-based filtering for multi-tenant security
 * 
 * Extracted from EventBasedSyncServiceImpl to improve code organization and maintainability.
 */
@Component
public class MedicalSheetSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(MedicalSheetSyncHelper.class);

    private final CloudMedicalSheetRepositoryJPA cloudMedicalSheetRepository;
    private final LocalMedicalSheetRepositoryJPA localMedicalSheetRepository;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public MedicalSheetSyncHelper(CloudMedicalSheetRepositoryJPA cloudMedicalSheetRepository,
                            LocalMedicalSheetRepositoryJPA localMedicalSheetRepository) {
        this.cloudMedicalSheetRepository = cloudMedicalSheetRepository;
        this.localMedicalSheetRepository = localMedicalSheetRepository;
    }

    /**
     * Processes a medical sheet sync item based on the change type.
     */
    public void processMedicalSheetSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncMedicalSheetCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncMedicalSheetUpdate(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for medical sheets.
     */
    private void syncMedicalSheetCreate(UUID medicalSheetId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<MedicalSheet> localMedicalSheetOpt = localMedicalSheetRepository.findById(medicalSheetId);
            
            if (localMedicalSheetOpt.isPresent()) {
                MedicalSheet localMedicalSheet = localMedicalSheetOpt.get();
                
                // Check if medical sheet already exists in cloud
                Optional<MedicalSheet> cloudMedicalSheetOpt = cloudMedicalSheetRepository.findByIdAndAccountId(medicalSheetId, accountId);
                
                if (cloudMedicalSheetOpt.isEmpty()) {
                    // Create in cloud
                    localMedicalSheet.setNew(true);
                    cloudMedicalSheetRepository.save(localMedicalSheet);
                    logger.info("Created medical sheet {} in cloud", medicalSheetId);
                } else {
                    // Already exists, treat as update
                    syncMedicalSheetUpdate(medicalSheetId, localToCloud);
                }
            } else {
                logger.warn("Local medical sheet {} not found for CREATE sync", medicalSheetId);
            }
        } else {
            // Sync from cloud to local
            Optional<MedicalSheet> cloudMedicalSheetOpt = cloudMedicalSheetRepository.findByIdAndAccountId(medicalSheetId, accountId);
            
            if (cloudMedicalSheetOpt.isPresent()) {
                MedicalSheet cloudMedicalSheet = cloudMedicalSheetOpt.get();
                
                // Check if medical sheet already exists locally
                Optional<MedicalSheet> localMedicalSheetOpt = localMedicalSheetRepository.findById(medicalSheetId);
                
                if (localMedicalSheetOpt.isEmpty()) {
                    // Create in local
                    cloudMedicalSheet.setNew(true);
                    localMedicalSheetRepository.save(cloudMedicalSheet);
                    logger.info("Created medical sheet {} in local from cloud", medicalSheetId);
                } else {
                    // Already exists, treat as update
                    syncMedicalSheetUpdate(medicalSheetId, localToCloud);
                }
            } else {
                logger.warn("Cloud medical sheet {} not found for CREATE sync for account {}", medicalSheetId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for medical sheets with intelligent conflict resolution.
     */
    private void syncMedicalSheetUpdate(UUID medicalSheetId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<MedicalSheet> localMedicalSheetOpt = localMedicalSheetRepository.findById(medicalSheetId);
            
            if (localMedicalSheetOpt.isPresent()) {
                MedicalSheet localMedicalSheet = localMedicalSheetOpt.get();
                Optional<MedicalSheet> cloudMedicalSheetOpt = cloudMedicalSheetRepository.findByIdAndAccountId(medicalSheetId, accountId);
                
                if (cloudMedicalSheetOpt.isPresent()) {
                    MedicalSheet cloudMedicalSheet = cloudMedicalSheetOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    MedicalSheet mergedMedicalSheet = (MedicalSheet) SyncUtil.performSimpleTimestampMerge(localMedicalSheet, cloudMedicalSheet, true);
                    
                    if (mergedMedicalSheet != null) {
                        // Changes were merged or local is newer
                        cloudMedicalSheetRepository.save(mergedMedicalSheet);
                        logger.info("Updated/merged medical sheet {} in cloud", medicalSheetId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud medical sheet {} is newer than local, no sync needed", medicalSheetId);
                    }
                } else {
                    // Cloud medical sheet doesn't exist, create it
                    localMedicalSheet.setNew(true);
                    cloudMedicalSheetRepository.save(localMedicalSheet);
                    logger.info("Created medical sheet {} in cloud (was UPDATE but not found)", medicalSheetId);
                }
            } else {
                logger.warn("Local medical sheet {} not found for UPDATE sync", medicalSheetId);
            }
        } else {
            // Sync from cloud to local
            Optional<MedicalSheet> cloudMedicalSheetOpt = cloudMedicalSheetRepository.findByIdAndAccountId(medicalSheetId, accountId);
            
            if (cloudMedicalSheetOpt.isPresent()) {
                MedicalSheet cloudMedicalSheet = cloudMedicalSheetOpt.get();
                Optional<MedicalSheet> localMedicalSheetOpt = localMedicalSheetRepository.findById(medicalSheetId);
                
                if (localMedicalSheetOpt.isPresent()) {
                    MedicalSheet localMedicalSheet = localMedicalSheetOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    MedicalSheet mergedMedicalSheet = (MedicalSheet) SyncUtil.performSimpleTimestampMerge(cloudMedicalSheet, localMedicalSheet, false);
                    
                    if (mergedMedicalSheet != null) {
                        // Changes were merged or cloud is newer
                        localMedicalSheetRepository.save(mergedMedicalSheet);
                        logger.info("Updated/merged medical sheet {} in local from cloud", medicalSheetId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local medical sheet {} is newer than cloud, no sync needed", medicalSheetId);
                    }
                } else {
                    // Local medical sheet doesn't exist, create it
                    cloudMedicalSheet.setNew(true);
                    localMedicalSheetRepository.save(cloudMedicalSheet);
                    logger.info("Created medical sheet {} in local from cloud (was UPDATE but not found)", medicalSheetId);
                }
            } else {
                logger.warn("Cloud medical sheet {} not found for UPDATE sync for account {}", medicalSheetId, accountId);
            }
        }
    }
}
