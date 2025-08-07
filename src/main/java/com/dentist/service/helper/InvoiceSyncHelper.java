package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.Invoice;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudInvoiceRepositoryJPA;
import com.dentist.repository.local.LocalInvoiceRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for handling Invoice entity synchronization between local and cloud databases.
 * 
 * This class encapsulates all invoice-specific sync logic including:
 * - CREATE/UPDATE/DELETE operations
 * - Intelligent conflict resolution with field-level merging
 * - Smart merge capabilities for concurrent updates
 * 
 * Extracted from EventBasedSyncServiceImpl to improve code organization and maintainability.
 */
@Component
public class InvoiceSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceSyncHelper.class);

    private final CloudInvoiceRepositoryJPA cloudInvoiceRepository;
    private final LocalInvoiceRepositoryJPA localInvoiceRepository;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public InvoiceSyncHelper(CloudInvoiceRepositoryJPA cloudInvoiceRepository,
                           LocalInvoiceRepositoryJPA localInvoiceRepository) {
        this.cloudInvoiceRepository = cloudInvoiceRepository;
        this.localInvoiceRepository = localInvoiceRepository;
    }

    /**
     * Processes an invoice sync item based on the change type.
     */
    public void processInvoiceSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncInvoiceCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncInvoiceUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncInvoiceDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for invoices.
     */
    private void syncInvoiceCreate(UUID invoiceId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Invoice> localInvoiceOpt = localInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
            
            if (localInvoiceOpt.isPresent()) {
                Invoice localInvoice = localInvoiceOpt.get();
                
                // Check if invoice already exists in cloud
                Optional<Invoice> cloudInvoiceOpt = cloudInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
                
                if (cloudInvoiceOpt.isEmpty()) {
                    // Create in cloud
                    cloudInvoiceRepository.save(localInvoice);
                    logger.info("Created invoice {} in cloud", invoiceId);
                } else {
                    // Already exists, treat as update
                    syncInvoiceUpdate(invoiceId, localToCloud);
                }
            } else {
                logger.warn("Local invoice {} not found for CREATE sync", invoiceId);
            }
        } else {
            // Sync from cloud to local
            Optional<Invoice> cloudInvoiceOpt = cloudInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
            
            if (cloudInvoiceOpt.isPresent()) {
                Invoice cloudInvoice = cloudInvoiceOpt.get();
                
                // Check if invoice already exists locally
                Optional<Invoice> localInvoiceOpt = localInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
                
                if (localInvoiceOpt.isEmpty()) {
                    // Create in local
                    localInvoiceRepository.save(cloudInvoice);
                    logger.info("Created invoice {} in local from cloud", invoiceId);
                } else {
                    // Already exists, treat as update
                    syncInvoiceUpdate(invoiceId, localToCloud);
                }
            } else {
                logger.warn("Cloud invoice {} not found for CREATE sync for account {}", invoiceId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for invoices with intelligent conflict resolution.
     */
    private void syncInvoiceUpdate(UUID invoiceId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Invoice> localInvoiceOpt = localInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
            
            if (localInvoiceOpt.isPresent()) {
                Invoice localInvoice = localInvoiceOpt.get();
                Optional<Invoice> cloudInvoiceOpt = cloudInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
                
                if (cloudInvoiceOpt.isPresent()) {
                    Invoice cloudInvoice = cloudInvoiceOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Invoice mergedInvoice = (Invoice) SyncUtil.performSimpleTimestampMerge(localInvoice, cloudInvoice, true);
                    
                    if (mergedInvoice != null) {

                        // Load the existing cloud treatment to enable proper cascade orphan removal
                        Invoice existingCloudInvoice = cloudInvoiceOpt.get();
                        
                        // Clear the existing collection to trigger orphan removal
                        existingCloudInvoice.getInvoiceItems().clear();
                        existingCloudInvoice.getInvoiceAmountAllocations().clear();
                        
                        // Copy the new collection from merged invoice
                        if (mergedInvoice.getInvoiceItems() != null) {
                            existingCloudInvoice.getInvoiceItems().addAll(mergedInvoice.getInvoiceItems());
                        }
                        
                     // Copy the new collection from merged invoice
                        if (mergedInvoice.getInvoiceAmountAllocations() != null) {
                            existingCloudInvoice.getInvoiceAmountAllocations().addAll(mergedInvoice.getInvoiceAmountAllocations());
                        }
                        
                        // Copy other fields from merged treatment
                        existingCloudInvoice.setInvNumber(mergedInvoice.getInvNumber());
                        existingCloudInvoice.setCustomer(mergedInvoice.getCustomer());
                        existingCloudInvoice.setIssueDate(mergedInvoice.getIssueDate());
                        existingCloudInvoice.setOriginalAmount(mergedInvoice.getOriginalAmount());
                        existingCloudInvoice.setInvDiscountType(mergedInvoice.getInvDiscountType());
                        existingCloudInvoice.setInvDiscountValue(mergedInvoice.getInvDiscountValue());
                        existingCloudInvoice.setInvDiscountAmount(mergedInvoice.getInvDiscountAmount());
                        existingCloudInvoice.setTotalItemDiscountAmount(mergedInvoice.getTotalItemDiscountAmount());
                        existingCloudInvoice.setTotalDiscountAmount(mergedInvoice.getTotalDiscountAmount());
                        existingCloudInvoice.setFinalAmount(mergedInvoice.getFinalAmount());
                        existingCloudInvoice.setStatus(mergedInvoice.getStatus());
                        existingCloudInvoice.setPaymentStatus(mergedInvoice.getPaymentStatus());
                        existingCloudInvoice.setUpdateDate(mergedInvoice.getUpdateDate());
                        
                        cloudInvoiceRepository.save(existingCloudInvoice);
                        logger.info("Updated/merged invoice {} in cloud", invoiceId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud invoice {} is newer than local, no sync needed", invoiceId);
                    }
                } else {
                    // Cloud invoice doesn't exist, create it
                    localInvoice.setNew(true);
                    cloudInvoiceRepository.save(localInvoice);
                    logger.info("Created invoice {} in cloud (was UPDATE but not found)", invoiceId);
                }
            } else {
                logger.warn("Local invoice {} not found for UPDATE sync", invoiceId);
            }
        } else {
            // Sync from cloud to local
            Optional<Invoice> cloudInvoiceOpt = cloudInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
            
            if (cloudInvoiceOpt.isPresent()) {
                Invoice cloudInvoice = cloudInvoiceOpt.get();
                Optional<Invoice> localInvoiceOpt = localInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
                
                if (localInvoiceOpt.isPresent()) {
                    Invoice localInvoice = localInvoiceOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Invoice mergedInvoice = (Invoice) SyncUtil.performSimpleTimestampMerge(cloudInvoice, localInvoice, false);
                    
                    if (mergedInvoice != null) {

                    	// Load the existing cloud treatment to enable proper cascade orphan removal
                        Invoice existingLocalInvoice = localInvoiceOpt.get();
                        
                        // Clear the existing collection to trigger orphan removal
                        existingLocalInvoice.getInvoiceItems().clear();
                        existingLocalInvoice.getInvoiceAmountAllocations().clear();
                        
                        // Copy the new collection from merged invoice
                        if (mergedInvoice.getInvoiceItems() != null) {
                            existingLocalInvoice.getInvoiceItems().addAll(mergedInvoice.getInvoiceItems());
                        }
                        
                     // Copy the new collection from merged invoice
                        if (mergedInvoice.getInvoiceAmountAllocations() != null) {
                            existingLocalInvoice.getInvoiceAmountAllocations().addAll(mergedInvoice.getInvoiceAmountAllocations());
                        }
                        
                        // Copy other fields from merged treatment
                        existingLocalInvoice.setInvNumber(mergedInvoice.getInvNumber());
                        existingLocalInvoice.setCustomer(mergedInvoice.getCustomer());
                        existingLocalInvoice.setIssueDate(mergedInvoice.getIssueDate());
                        existingLocalInvoice.setOriginalAmount(mergedInvoice.getOriginalAmount());
                        existingLocalInvoice.setInvDiscountType(mergedInvoice.getInvDiscountType());
                        existingLocalInvoice.setInvDiscountValue(mergedInvoice.getInvDiscountValue());
                        existingLocalInvoice.setInvDiscountAmount(mergedInvoice.getInvDiscountAmount());
                        existingLocalInvoice.setTotalItemDiscountAmount(mergedInvoice.getTotalItemDiscountAmount());
                        existingLocalInvoice.setTotalDiscountAmount(mergedInvoice.getTotalDiscountAmount());
                        existingLocalInvoice.setFinalAmount(mergedInvoice.getFinalAmount());
                        existingLocalInvoice.setStatus(mergedInvoice.getStatus());
                        existingLocalInvoice.setPaymentStatus(mergedInvoice.getPaymentStatus());
                        existingLocalInvoice.setUpdateDate(mergedInvoice.getUpdateDate());
                        
                        localInvoiceRepository.save(existingLocalInvoice);
                        logger.info("Updated/merged invoice {} in local from cloud", invoiceId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local invoice {} is newer than cloud, no sync needed", invoiceId);
                    }
                } else {
                    // Local invoice doesn't exist, create it
                    cloudInvoice.setNew(true);
                    localInvoiceRepository.save(cloudInvoice);
                    logger.info("Created invoice {} in local from cloud (was UPDATE but not found)", invoiceId);
                }
            } else {
                logger.warn("Cloud invoice {} not found for UPDATE sync for account {}", invoiceId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for invoices.
     */
    private void syncInvoiceDelete(UUID invoiceId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<Invoice> cloudInvoiceOpt = cloudInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
            
            if (cloudInvoiceOpt.isPresent()) {
                Invoice cloudInvoice = cloudInvoiceOpt.get();
                cloudInvoice.setStatus(Invoice.STATUS_DELETED);
                cloudInvoiceRepository.save(cloudInvoice);
                logger.info("Marked invoice {} as deleted in cloud", invoiceId);
            } else {
                logger.info("Invoice {} not found in cloud for DELETE sync (already deleted?)", invoiceId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<Invoice> localInvoiceOpt = localInvoiceRepository.findByIdAndAccountId(invoiceId, accountId);
            
            if (localInvoiceOpt.isPresent()) {
                Invoice localInvoice = localInvoiceOpt.get();
                localInvoice.setStatus(Invoice.STATUS_DELETED);
                localInvoiceRepository.save(localInvoice);
                logger.info("Marked invoice {} as deleted in local from cloud", invoiceId);
            } else {
                logger.info("Invoice {} not found in local for DELETE sync (already deleted?)", invoiceId);
            }
        }
    }
}
