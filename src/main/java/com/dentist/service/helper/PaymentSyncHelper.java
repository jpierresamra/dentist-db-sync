package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.Payment;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudPaymentRepositoryJPA;
import com.dentist.repository.local.LocalPaymentRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for Payment entity synchronization between local and cloud databases.
 * Provides smart merge capabilities with field-level conflict resolution and account-based filtering.
 * 
 * Features:
 * - Account-based multi-tenant security
 * - Smart merge with comprehensive field handling
 * - Payment-specific logic (amount, method, currency)
 * - Enhanced logging and error handling
 * 
 * @author Generated Helper Pattern
 * @version 1.0
 */
@Component
public class PaymentSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSyncHelper.class);
    
    private final CloudPaymentRepositoryJPA cloudPaymentRepository;
    private final LocalPaymentRepositoryJPA localPaymentRepository;
    
    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public PaymentSyncHelper(
            CloudPaymentRepositoryJPA cloudPaymentRepository,
            LocalPaymentRepositoryJPA localPaymentRepository) {
        this.cloudPaymentRepository = cloudPaymentRepository;
        this.localPaymentRepository = localPaymentRepository;
    }

    /**
     * Process a payment sync item with smart merge capabilities
     * @param syncItem the sync queue item
     * @param localToCloud true if syncing from local to cloud, false if cloud to local
     * @throws Exception if sync fails
     */
    public void processPaymentSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncPaymentCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncPaymentUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncPaymentDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for payments.
     */
    private void syncPaymentCreate(UUID paymentId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Payment> localPaymentOpt = localPaymentRepository.findByIdAndAccountId(paymentId, accountId);
            
            if (localPaymentOpt.isPresent()) {
                Payment localPayment = localPaymentOpt.get();
                
                // Check if payment already exists in cloud
                Optional<Payment> cloudPaymentOpt = cloudPaymentRepository.findByIdAndAccountId(paymentId, accountId);
                
                if (cloudPaymentOpt.isEmpty()) {
                    // Create in cloud
                    localPayment.setNew(true);
                    cloudPaymentRepository.save(localPayment);
                    logger.info("Created payment {} in cloud", paymentId);
                } else {
                    // Already exists, treat as update
                    syncPaymentUpdate(paymentId, localToCloud);
                }
            } else {
                logger.warn("Local payment {} not found for CREATE sync", paymentId);
            }
        } else {
            // Sync from cloud to local
            Optional<Payment> cloudPaymentOpt = cloudPaymentRepository.findByIdAndAccountId(paymentId, accountId);
            
            if (cloudPaymentOpt.isPresent()) {
                Payment cloudPayment = cloudPaymentOpt.get();
                
                // Check if payment already exists locally
                Optional<Payment> localPaymentOpt = localPaymentRepository.findByIdAndAccountId(paymentId, accountId);
                
                if (localPaymentOpt.isEmpty()) {
                    // Create in local
                    cloudPayment.setNew(true);
                    localPaymentRepository.save(cloudPayment);
                    logger.info("Created payment {} in local from cloud", paymentId);
                } else {
                    // Already exists, treat as update
                    syncPaymentUpdate(paymentId, localToCloud);
                }
            } else {
                logger.warn("Cloud payment {} not found for CREATE sync for account {}", paymentId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for payments with intelligent conflict resolution.
     */
    private void syncPaymentUpdate(UUID paymentId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Payment> localPaymentOpt = localPaymentRepository.findByIdAndAccountId(paymentId, accountId);
            
            if (localPaymentOpt.isPresent()) {
                Payment localPayment = localPaymentOpt.get();
                Optional<Payment> cloudPaymentOpt = cloudPaymentRepository.findByIdAndAccountId(paymentId, accountId);
                
                if (cloudPaymentOpt.isPresent()) {
                    Payment cloudPayment = cloudPaymentOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Payment mergedPayment = (Payment) SyncUtil.performSimpleTimestampMerge(localPayment, cloudPayment, true);
                    
                    if (mergedPayment != null) {
                        // Changes were merged or local is newer
                        cloudPaymentRepository.save(mergedPayment);
                        logger.info("Updated/merged payment {} in cloud", paymentId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud payment {} is newer than local, no sync needed", paymentId);
                    }
                } else {
                    // Cloud payment doesn't exist, create it
                    localPayment.setNew(true);
                    cloudPaymentRepository.save(localPayment);
                    logger.info("Created payment {} in cloud (was UPDATE but not found)", paymentId);
                }
            } else {
                logger.warn("Local payment {} not found for UPDATE sync", paymentId);
            }
        } else {
            // Sync from cloud to local
            Optional<Payment> cloudPaymentOpt = cloudPaymentRepository.findByIdAndAccountId(paymentId, accountId);
            
            if (cloudPaymentOpt.isPresent()) {
                Payment cloudPayment = cloudPaymentOpt.get();
                Optional<Payment> localPaymentOpt = localPaymentRepository.findByIdAndAccountId(paymentId, accountId);
                
                if (localPaymentOpt.isPresent()) {
                    Payment localPayment = localPaymentOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Payment mergedPayment = (Payment) SyncUtil.performSimpleTimestampMerge(cloudPayment, localPayment, false);
                    
                    if (mergedPayment != null) {
                        // Changes were merged or cloud is newer
                        localPaymentRepository.save(mergedPayment);
                        logger.info("Updated/merged payment {} in local from cloud", paymentId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local payment {} is newer than cloud, no sync needed", paymentId);
                    }
                } else {
                    // Local payment doesn't exist, create it
                    cloudPayment.setNew(true);
                    localPaymentRepository.save(cloudPayment);
                    logger.info("Created payment {} in local from cloud (was UPDATE but not found)", paymentId);
                }
            } else {
                logger.warn("Cloud payment {} not found for UPDATE sync for account {}", paymentId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for payments.
     */
    private void syncPaymentDelete(UUID paymentId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<Payment> cloudPaymentOpt = cloudPaymentRepository.findByIdAndAccountId(paymentId, accountId);
            
            if (cloudPaymentOpt.isPresent()) {
                Payment cloudPayment = cloudPaymentOpt.get();
                cloudPayment.setStatus(Payment.STATUS_DELETED);
                cloudPaymentRepository.save(cloudPayment);
                logger.info("Marked payment {} as deleted in cloud", paymentId);
            } else {
                logger.info("Payment {} not found in cloud for DELETE sync (already deleted?)", paymentId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<Payment> localPaymentOpt = localPaymentRepository.findByIdAndAccountId(paymentId, accountId);
            
            if (localPaymentOpt.isPresent()) {
                Payment localPayment = localPaymentOpt.get();
                localPayment.setStatus(Payment.STATUS_DELETED);
                localPaymentRepository.save(localPayment);
                logger.info("Marked payment {} as deleted in local from cloud", paymentId);
            } else {
                logger.info("Payment {} not found in local for DELETE sync (already deleted?)", paymentId);
            }
        }
    }
}
