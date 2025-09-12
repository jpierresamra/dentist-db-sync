package com.dentist.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dentist.beans.SyncQueueItem;
import com.dentist.repository.cloud.CloudSyncQueueRepositoryJPA;
import com.dentist.repository.local.LocalSyncQueueRepositoryJPA;
import com.dentist.service.EventBasedSyncService;
import com.dentist.service.helper.AccountSyncHelper;
import com.dentist.service.helper.AppointmentSyncHelper;
import com.dentist.service.helper.ClassTypeSyncHelper;
import com.dentist.service.helper.ClinicSyncHelper;
import com.dentist.service.helper.ConfigAccountSettingSyncHelper;
import com.dentist.service.helper.ConfigClinicSettingSyncHelper;
import com.dentist.service.helper.CustomerSyncHelper;
import com.dentist.service.helper.InvoiceSyncHelper;
import com.dentist.service.helper.MedicalSheetSyncHelper;
import com.dentist.service.helper.OperationSyncHelper;
import com.dentist.service.helper.PaymentSyncHelper;
import com.dentist.service.helper.ProcedureSyncHelper;
import com.dentist.service.helper.RecallSyncHelper;
import com.dentist.service.helper.TreatmentSyncHelper;
import com.dentist.service.helper.UserSyncHelper;

@Service
public class EventBasedSyncServiceImpl implements EventBasedSyncService {

    private static final Logger logger = LoggerFactory.getLogger(EventBasedSyncServiceImpl.class);
    private static final int MAX_RETRY_COUNT = 3;
    
    private final LocalSyncQueueRepositoryJPA localSyncQueueRepository;
    private final CloudSyncQueueRepositoryJPA cloudSyncQueueRepository;
    
    // Sync helpers
    private final InvoiceSyncHelper invoiceSyncHelper;
    private final CustomerSyncHelper customerSyncHelper;
    private final RecallSyncHelper recallSyncHelper;
    private final MedicalSheetSyncHelper medicalSheetSyncHelper;
    private final ClassTypeSyncHelper classTypeSyncHelper;
    private final ClinicSyncHelper clinicSyncHelper;
    private final ConfigClinicSettingSyncHelper configClinicSettingSyncHelper;
    private final ConfigAccountSettingSyncHelper configAccountSettingSyncHelper;
    private final ProcedureSyncHelper procedureSyncHelper;
    private final AccountSyncHelper accountSyncHelper;
    private final PaymentSyncHelper paymentSyncHelper;
    private final TreatmentSyncHelper treatmentSyncHelper;
    private final AppointmentSyncHelper appointmentSyncHelper;
    private final OperationSyncHelper operationSyncHelper;
    private final UserSyncHelper userSyncHelper;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public EventBasedSyncServiceImpl(
            LocalSyncQueueRepositoryJPA localSyncQueueRepository,
            CloudSyncQueueRepositoryJPA cloudSyncQueueRepository,
            InvoiceSyncHelper invoiceSyncHelper,
            CustomerSyncHelper customerSyncHelper,
            RecallSyncHelper recallSyncHelper,
            MedicalSheetSyncHelper medicalSheetSyncHelper,
            ClassTypeSyncHelper classTypeSyncHelper,
            ClinicSyncHelper clinicSyncHelper,
            ConfigAccountSettingSyncHelper configAccountSettingSyncHelper,
            ConfigClinicSettingSyncHelper configClinicSettingSyncHelper,
            ProcedureSyncHelper procedureSyncHelper,
            AccountSyncHelper accountSyncHelper,
            PaymentSyncHelper paymentSyncHelper,
            TreatmentSyncHelper treatmentSyncHelper,
            AppointmentSyncHelper appointmentSyncHelper,
            OperationSyncHelper operationSyncHelper,
            UserSyncHelper userSyncHelper) {
        this.localSyncQueueRepository = localSyncQueueRepository;
        this.cloudSyncQueueRepository = cloudSyncQueueRepository;
        this.invoiceSyncHelper = invoiceSyncHelper;
        this.customerSyncHelper = customerSyncHelper;
        this.recallSyncHelper = recallSyncHelper;
        this.medicalSheetSyncHelper = medicalSheetSyncHelper;
        this.classTypeSyncHelper = classTypeSyncHelper;
        this.clinicSyncHelper = clinicSyncHelper;
        this.configAccountSettingSyncHelper = configAccountSettingSyncHelper;
        this.configClinicSettingSyncHelper = configClinicSettingSyncHelper;
        this.procedureSyncHelper = procedureSyncHelper;
        this.accountSyncHelper = accountSyncHelper;
        this.paymentSyncHelper = paymentSyncHelper;
        this.treatmentSyncHelper = treatmentSyncHelper;
        this.appointmentSyncHelper = appointmentSyncHelper;
        this.operationSyncHelper = operationSyncHelper;
        this.userSyncHelper = userSyncHelper;
    }

    @Override
    public void processQueuedSyncEvents() {
        logger.info("Starting to process queued sync events for account {}", accountId);
        
        // Process local sync queue (upward sync to cloud)
        processLocalToCloudSync();
        
        // Process cloud sync queue (downward sync to local)
        processCloudToLocalSync();
        
        // Clean up old processed items (older than 7 days)
        cleanupOldProcessedItems();
        
        logger.info("Completed processing queued sync events for account {}", accountId);
    }

    private void processLocalToCloudSync() {
        logger.info("Processing local to cloud sync for account {}", accountId);
        
        // Get all unprocessed sync items for this account from local queue
        List<SyncQueueItem> unprocessedItems = localSyncQueueRepository.findUnprocessedItemsByAccountId(accountId);
        
        logger.info("Found {} unprocessed local sync items for account {}", unprocessedItems.size(), accountId);
        
        for (SyncQueueItem syncItem : unprocessedItems) {
            try {
                processSyncItem(syncItem, true); // true = local to cloud
                
                // Mark as processed
                localSyncQueueRepository.markItemAsProcessed(syncItem.getId(), Instant.now());
                logger.debug("Successfully processed local sync item: {} {} {}", 
                    syncItem.getEntityType(), syncItem.getEntityId(), syncItem.getChangeType());
                
            } catch (Exception e) {
                handleLocalSyncItemError(syncItem, e);
            }
        }
    }

    private void processCloudToLocalSync() {
        logger.info("Processing cloud to local sync for account {}", accountId);
        
        // Get all unprocessed sync items for this account from cloud queue
        List<SyncQueueItem> unprocessedItems = cloudSyncQueueRepository
            .findUnprocessedItemsByAccountId(accountId);
        
        logger.info("Found {} unprocessed cloud sync items for account {}", unprocessedItems.size(), accountId);
        
        for (SyncQueueItem syncItem : unprocessedItems) {
            try {
                processSyncItem(syncItem, false); // false = cloud to local
                
                // Mark as processed in cloud queue
                cloudSyncQueueRepository.markItemAsProcessed(syncItem.getId(), Instant.now());
                logger.debug("Successfully processed cloud sync item: {} {} {}", 
                    syncItem.getEntityType(), syncItem.getEntityId(), syncItem.getChangeType());
                
            } catch (Exception e) {
                handleCloudSyncItemError(syncItem, e);
            }
        }
    }

    private void processSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        switch (syncItem.getEntityType()) {
            case SyncQueueItem.ENTITY_TYPE_INVOICE:
                invoiceSyncHelper.processInvoiceSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_PAYMENT:
                paymentSyncHelper.processPaymentSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_TREATMENT:
                treatmentSyncHelper.processTreatmentSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_APPOINTMENT:
                appointmentSyncHelper.processAppointmentSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_CUSTOMER:
                customerSyncHelper.processCustomerSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_USER:
                userSyncHelper.processUserSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_ACCOUNT:
                accountSyncHelper.processAccountSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_OPERATION:
                operationSyncHelper.processOperationSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_PROCEDURE:
                procedureSyncHelper.processProcedureSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_CLASS_TYPE:
                classTypeSyncHelper.processClassTypeSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_MEDICAL_SHEET:
                medicalSheetSyncHelper.processMedicalSheetSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_RECALL:
                recallSyncHelper.processRecallSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_CLINIC:
                clinicSyncHelper.processClinicSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_CONFIG_ACCOUNT_SETTING:
            	configAccountSettingSyncHelper.processConfigSettingSyncItem(syncItem, localToCloud);
                break;
            case SyncQueueItem.ENTITY_TYPE_CONFIG_CLINIC_SETTING:
            	configClinicSettingSyncHelper.processConfigSettingSyncItem(syncItem, localToCloud);
                break;
            default:
                logger.warn("Unknown entity type: {} for sync item {}", 
                    syncItem.getEntityType(), syncItem.getId());
        }
    }

    private void handleLocalSyncItemError(SyncQueueItem syncItem, Exception e) {
        logger.error("Error processing local sync item {}: {}", syncItem.getId(), e.getMessage(), e);
        
        if (syncItem.getRetryCount() < MAX_RETRY_COUNT) {
            // Increment retry count and update error message
            localSyncQueueRepository.incrementRetryCount(syncItem.getId(), e.getMessage());
            logger.info("Marked local sync item {} for retry (attempt {})", 
                syncItem.getId(), syncItem.getRetryCount() + 1);
        } else {
            // Max retries reached, mark as processed with error
            localSyncQueueRepository.markItemAsProcessed(syncItem.getId(), Instant.now());
            logger.error("Max retries reached for local sync item {}, marking as processed with error", 
                syncItem.getId());
        }
    }

    private void handleCloudSyncItemError(SyncQueueItem syncItem, Exception e) {
        logger.error("Error processing cloud sync item {}: {}", syncItem.getId(), e.getMessage(), e);
        
        if (syncItem.getRetryCount() < MAX_RETRY_COUNT) {
            // Increment retry count and update error message
            cloudSyncQueueRepository.incrementRetryCount(syncItem.getId(), e.getMessage());
            logger.info("Marked cloud sync item {} for retry (attempt {})", 
                syncItem.getId(), syncItem.getRetryCount() + 1);
        } else {
            // Max retries reached, mark as processed with error
            cloudSyncQueueRepository.markItemAsProcessed(syncItem.getId(), Instant.now());
            logger.error("Max retries reached for cloud sync item {}, marking as processed with error", 
                syncItem.getId());
        }
    }

    private void cleanupOldProcessedItems() {
        try {
            Instant cutoffDate = Instant.now().minus(7, ChronoUnit.DAYS);
            localSyncQueueRepository.deleteOldProcessedItemsByAccountId(cutoffDate, accountId);
            logger.debug("Cleaned up old processed sync items older than {}", cutoffDate);
        } catch (Exception e) {
            logger.error("Error cleaning up old processed items: {}", e.getMessage(), e);
        }
    }

    @Override
    public long getUnprocessedItemCount() {
        return localSyncQueueRepository.countUnprocessedItemsByAccountId(accountId);
    }
}