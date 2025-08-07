package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.Customer;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudCustomerRepositoryJPA;
import com.dentist.repository.local.LocalCustomerRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for handling Customer entity synchronization between local and cloud databases.
 * 
 * This class encapsulates all customer-specific sync logic including:
 * - CREATE/UPDATE/DELETE operations
 * - Intelligent conflict resolution with field-level merging
 * - Smart merge capabilities for concurrent updates
 * - Account-based filtering for multi-tenant security
 * 
 * Extracted from EventBasedSyncServiceImpl to improve code organization and maintainability.
 */
@Component
public class CustomerSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(CustomerSyncHelper.class);

    private final CloudCustomerRepositoryJPA cloudCustomerRepository;
    private final LocalCustomerRepositoryJPA localCustomerRepository;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public CustomerSyncHelper(CloudCustomerRepositoryJPA cloudCustomerRepository,
                            LocalCustomerRepositoryJPA localCustomerRepository) {
        this.cloudCustomerRepository = cloudCustomerRepository;
        this.localCustomerRepository = localCustomerRepository;
    }

    /**
     * Processes a customer sync item based on the change type.
     */
    public void processCustomerSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncCustomerCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncCustomerUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncCustomerDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for customers.
     */
    private void syncCustomerCreate(UUID customerId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Customer> localCustomerOpt = localCustomerRepository.findByIdAndAccountId(customerId, accountId);
            
            if (localCustomerOpt.isPresent()) {
                Customer localCustomer = localCustomerOpt.get();
                
                // Check if customer already exists in cloud
                Optional<Customer> cloudCustomerOpt = cloudCustomerRepository.findByIdAndAccountId(customerId, accountId);
                
                if (cloudCustomerOpt.isEmpty()) {
                    // Create in cloud
                    localCustomer.setNew(true);
                    cloudCustomerRepository.save(localCustomer);
                    logger.info("Created customer {} in cloud", customerId);
                } else {
                    // Already exists, treat as update
                    syncCustomerUpdate(customerId, localToCloud);
                }
            } else {
                logger.warn("Local customer {} not found for CREATE sync", customerId);
            }
        } else {
            // Sync from cloud to local
            Optional<Customer> cloudCustomerOpt = cloudCustomerRepository.findByIdAndAccountId(customerId, accountId);
            
            if (cloudCustomerOpt.isPresent()) {
                Customer cloudCustomer = cloudCustomerOpt.get();
                
                // Check if customer already exists locally
                Optional<Customer> localCustomerOpt = localCustomerRepository.findByIdAndAccountId(customerId, accountId);
                
                if (localCustomerOpt.isEmpty()) {
                    // Create in local
                    cloudCustomer.setNew(true);
                    localCustomerRepository.save(cloudCustomer);
                    logger.info("Created customer {} in local from cloud", customerId);
                } else {
                    // Already exists, treat as update
                    syncCustomerUpdate(customerId, localToCloud);
                }
            } else {
                logger.warn("Cloud customer {} not found for CREATE sync for account {}", customerId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for customers with intelligent conflict resolution.
     */
    private void syncCustomerUpdate(UUID customerId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<Customer> localCustomerOpt = localCustomerRepository.findByIdAndAccountId(customerId, accountId);
            
            if (localCustomerOpt.isPresent()) {
                Customer localCustomer = localCustomerOpt.get();
                Optional<Customer> cloudCustomerOpt = cloudCustomerRepository.findByIdAndAccountId(customerId, accountId);
                
                if (cloudCustomerOpt.isPresent()) {
                    Customer cloudCustomer = cloudCustomerOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Customer mergedCustomer = (Customer) SyncUtil.performSimpleTimestampMerge(localCustomer, cloudCustomer, true);
                    
                    if (mergedCustomer != null) {
                        // Changes were merged or local is newer
                        cloudCustomerRepository.save(mergedCustomer);
                        logger.info("Updated/merged customer {} in cloud", customerId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud customer {} is newer than local, no sync needed", customerId);
                    }
                } else {
                    // Cloud customer doesn't exist, create it
                    localCustomer.setNew(true);
                    cloudCustomerRepository.save(localCustomer);
                    logger.info("Created customer {} in cloud (was UPDATE but not found)", customerId);
                }
            } else {
                logger.warn("Local customer {} not found for UPDATE sync", customerId);
            }
        } else {
            // Sync from cloud to local
            Optional<Customer> cloudCustomerOpt = cloudCustomerRepository.findByIdAndAccountId(customerId, accountId);
            
            if (cloudCustomerOpt.isPresent()) {
                Customer cloudCustomer = cloudCustomerOpt.get();
                Optional<Customer> localCustomerOpt = localCustomerRepository.findByIdAndAccountId(customerId, accountId);
                
                if (localCustomerOpt.isPresent()) {
                    Customer localCustomer = localCustomerOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    Customer mergedCustomer = (Customer) SyncUtil.performSimpleTimestampMerge(cloudCustomer, localCustomer, false);
                    
                    if (mergedCustomer != null) {
                        // Changes were merged or cloud is newer
                        localCustomerRepository.save(mergedCustomer);
                        logger.info("Updated/merged customer {} in local from cloud", customerId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local customer {} is newer than cloud, no sync needed", customerId);
                    }
                } else {
                    // Local customer doesn't exist, create it
                    cloudCustomer.setNew(true);
                    localCustomerRepository.save(cloudCustomer);
                    logger.info("Created customer {} in local from cloud (was UPDATE but not found)", customerId);
                }
            } else {
                logger.warn("Cloud customer {} not found for UPDATE sync for account {}", customerId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for customers.
     */
    private void syncCustomerDelete(UUID customerId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<Customer> cloudCustomerOpt = cloudCustomerRepository.findByIdAndAccountId(customerId, accountId);
            
            if (cloudCustomerOpt.isPresent()) {
                Customer cloudCustomer = cloudCustomerOpt.get();
                cloudCustomer.setStatus(Customer.STATUS_DELETED);
                cloudCustomerRepository.save(cloudCustomer);
                logger.info("Marked customer {} as deleted in cloud", customerId);
            } else {
                logger.info("Customer {} not found in cloud for DELETE sync (already deleted?)", customerId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<Customer> localCustomerOpt = localCustomerRepository.findByIdAndAccountId(customerId, accountId);
            
            if (localCustomerOpt.isPresent()) {
                Customer localCustomer = localCustomerOpt.get();
                localCustomer.setStatus(Customer.STATUS_DELETED);
                localCustomerRepository.save(localCustomer);
                logger.info("Marked customer {} as deleted in local from cloud", customerId);
            } else {
                logger.info("Customer {} not found in local for DELETE sync (already deleted?)", customerId);
            }
        }
    }
}
