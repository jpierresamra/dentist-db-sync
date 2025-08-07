package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.Appointment;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudAppointmentRepositoryJPA;
import com.dentist.repository.local.LocalAppointmentRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for handling Appointment entity synchronization between local and cloud databases.
 * 
 * This class encapsulates all appointment-specific sync logic including:
 * - CREATE/UPDATE/DELETE operations
 * - Intelligent conflict resolution with field-level merging
 * - Smart merge capabilities for concurrent updates
 * - Account-based filtering for multi-tenant security
 * 
 * Extracted from EventBasedSyncServiceImpl to improve code organization and maintainability.
 */
@Component
public class AppointmentSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentSyncHelper.class);

    private final CloudAppointmentRepositoryJPA cloudAppointmentRepository;
    private final LocalAppointmentRepositoryJPA localAppointmentRepository;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public AppointmentSyncHelper(CloudAppointmentRepositoryJPA cloudAppointmentRepository,
                            LocalAppointmentRepositoryJPA localAppointmentRepository) {
        this.cloudAppointmentRepository = cloudAppointmentRepository;
        this.localAppointmentRepository = localAppointmentRepository;
    }

    /**
     * Processes an appointment sync item based on the change type.
     */
    public void processAppointmentSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncAppointmentCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncAppointmentUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncAppointmentDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for appointments.
     */
    private void syncAppointmentCreate(UUID appointmentId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Appointment> localAppointmentOpt = localAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
            
            if (localAppointmentOpt.isPresent()) {
                Appointment localAppointment = localAppointmentOpt.get();
                
                // Check if appointment already exists in cloud
                Optional<Appointment> cloudAppointmentOpt = cloudAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
                
                if (cloudAppointmentOpt.isEmpty()) {
                    // Create in cloud
                    localAppointment.setNew(true);
                    cloudAppointmentRepository.save(localAppointment);
                    logger.info("Created appointment {} in cloud", appointmentId);
                } else {
                    // Already exists, treat as update
                    syncAppointmentUpdate(appointmentId, localToCloud);
                }
            } else {
                logger.warn("Local appointment {} not found for CREATE sync", appointmentId);
            }
        } else {
            // Sync from cloud to local
            Optional<Appointment> cloudAppointmentOpt = cloudAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
            
            if (cloudAppointmentOpt.isPresent()) {
                Appointment cloudAppointment = cloudAppointmentOpt.get();
                
                // Check if appointment already exists locally
                Optional<Appointment> localAppointmentOpt = localAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
                
                if (localAppointmentOpt.isEmpty()) {
                    // Create in local
                    cloudAppointment.setNew(true);
                    localAppointmentRepository.save(cloudAppointment);
                    logger.info("Created appointment {} in local from cloud", appointmentId);
                } else {
                    // Already exists, treat as update
                    syncAppointmentUpdate(appointmentId, localToCloud);
                }
            } else {
                logger.warn("Cloud appointment {} not found for CREATE sync for account {}", appointmentId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for appointments with intelligent conflict resolution.
     */
    private void syncAppointmentUpdate(UUID appointmentId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Appointment> localAppointmentOpt = localAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
            
            if (localAppointmentOpt.isPresent()) {
                Appointment localAppointment = localAppointmentOpt.get();
                Optional<Appointment> cloudAppointmentOpt = cloudAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
                
                if (cloudAppointmentOpt.isPresent()) {
                    Appointment cloudAppointment = cloudAppointmentOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Appointment mergedAppointment = (Appointment) SyncUtil.performSimpleTimestampMerge(localAppointment, cloudAppointment, true);
                    
                    if (mergedAppointment != null) {
                        // Changes were merged or local is newer
                        cloudAppointmentRepository.save(mergedAppointment);
                        logger.info("Updated/merged appointment {} in cloud", appointmentId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud appointment {} is newer than local, no sync needed", appointmentId);
                    }
                } else {
                    // Cloud appointment doesn't exist, create it
                    localAppointment.setNew(true);
                    cloudAppointmentRepository.save(localAppointment);
                    logger.info("Created appointment {} in cloud (was UPDATE but not found)", appointmentId);
                }
            } else {
                logger.warn("Local appointment {} not found for UPDATE sync", appointmentId);
            }
        } else {
            // Sync from cloud to local
            Optional<Appointment> cloudAppointmentOpt = cloudAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
            
            if (cloudAppointmentOpt.isPresent()) {
                Appointment cloudAppointment = cloudAppointmentOpt.get();
                Optional<Appointment> localAppointmentOpt = localAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
                
                if (localAppointmentOpt.isPresent()) {
                    Appointment localAppointment = localAppointmentOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Appointment mergedAppointment = (Appointment) SyncUtil.performSimpleTimestampMerge(cloudAppointment, localAppointment, false);
                    
                    if (mergedAppointment != null) {
                        // Changes were merged or cloud is newer
                        localAppointmentRepository.save(mergedAppointment);
                        logger.info("Updated/merged appointment {} in local from cloud", appointmentId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local appointment {} is newer than cloud, no sync needed", appointmentId);
                    }
                } else {
                    // Local appointment doesn't exist, create it
                    cloudAppointment.setNew(true);
                    localAppointmentRepository.save(cloudAppointment);
                    logger.info("Created appointment {} in local from cloud (was UPDATE but not found)", appointmentId);
                }
            } else {
                logger.warn("Cloud appointment {} not found for UPDATE sync for account {}", appointmentId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for appointments.
     */
    private void syncAppointmentDelete(UUID appointmentId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<Appointment> cloudAppointmentOpt = cloudAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
            
            if (cloudAppointmentOpt.isPresent()) {
                Appointment cloudAppointment = cloudAppointmentOpt.get();
                cloudAppointment.setStatus(Appointment.STATUS_DELETED);
                cloudAppointmentRepository.save(cloudAppointment);
                logger.info("Marked appointment {} as deleted in cloud", appointmentId);
            } else {
                logger.info("Appointment {} not found in cloud for DELETE sync (already deleted?)", appointmentId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<Appointment> localAppointmentOpt = localAppointmentRepository.findByIdAndAccountId(appointmentId, accountId);
            
            if (localAppointmentOpt.isPresent()) {
                Appointment localAppointment = localAppointmentOpt.get();
                localAppointment.setStatus(Appointment.STATUS_DELETED);
                localAppointmentRepository.save(localAppointment);
                logger.info("Marked appointment {} as deleted in local from cloud", appointmentId);
            } else {
                logger.info("Appointment {} not found in local for DELETE sync (already deleted?)", appointmentId);
            }
        }
    }
}
