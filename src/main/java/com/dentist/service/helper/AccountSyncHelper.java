package com.dentist.service.helper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.Account;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudAccountRepositoryJPA;
import com.dentist.repository.local.LocalAccountRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for Account entity synchronization between local and cloud databases.
 * Provides smart merge capabilities with field-level conflict resolution.
 * 
 * Note: Account entity uses Integer ID instead of UUID, and has different filtering approach
 * since accounts are the top-level tenant entities.
 * 
 * Features:
 * - Account-specific filtering using findByAccountId
 * - Smart merge with comprehensive account field handling
 * - Enhanced logging and error handling
 * - Special handling for account settings and configuration
 * 
 * @author Generated Helper Pattern
 * @version 1.0
 */
@Component
public class AccountSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(AccountSyncHelper.class);
    
    private final CloudAccountRepositoryJPA cloudAccountRepository;
    private final LocalAccountRepositoryJPA localAccountRepository;
    
    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public AccountSyncHelper(
            CloudAccountRepositoryJPA cloudAccountRepository,
            LocalAccountRepositoryJPA localAccountRepository) {
        this.cloudAccountRepository = cloudAccountRepository;
        this.localAccountRepository = localAccountRepository;
    }

    /**
     * Process an account sync item with smart merge capabilities
     * @param syncItem the sync queue item
     * @param localToCloud true if syncing from local to cloud, false if cloud to local
     * @throws Exception if sync fails
     */
    public void processAccountSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case UPDATE:
                syncAccountUpdate(syncItem.getAccountId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    private void syncAccountUpdate(int entityAccountId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            Optional<Account> localAccountOpt = localAccountRepository.findById(entityAccountId);
            
            if (localAccountOpt.isPresent()) {
            	Account localAccount = localAccountOpt.get();
                Optional<Account> cloudAccountOpt = cloudAccountRepository.findByAccountId( accountId);
                
                if (cloudAccountOpt.isPresent()) {
                	Account cloudAccount = cloudAccountOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                	Account mergedAccount = (Account) SyncUtil.performSimpleTimestampMerge(localAccount, cloudAccount, true);
                    
                    if (mergedAccount != null) {
                        // Changes were merged or local is newer
                        cloudAccountRepository.save(mergedAccount);
                        logger.info("Updated/merged Account {} in cloud", accountId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud Account {} is newer than local, no sync needed", accountId);
                    }
                } else {
                    // Cloud Account doesn't exist, create it
                    localAccount.setNew(true);
                    cloudAccountRepository.save(localAccount);
                    logger.info("Created Account {} in cloud (was UPDATE but not found)", accountId);
                }
            } else {
                logger.warn("Local Account {} not found for UPDATE sync", entityAccountId);
            }
        } else {
            // Cloud to local sync
            Optional<Account> cloudAccountOpt = cloudAccountRepository.findByAccountId(entityAccountId);
            
            if (cloudAccountOpt.isPresent()) {
            	Account cloudAccount = cloudAccountOpt.get();
                Optional<Account> localAccountOpt = localAccountRepository.findById(accountId);
                
                if (localAccountOpt.isPresent()) {
                	Account localAccount = localAccountOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                	Account mergedAccount = (Account) SyncUtil.performSimpleTimestampMerge(cloudAccount, localAccount, false);
                    
                    if (mergedAccount != null) {
                        // Changes were merged or cloud is newer
                        localAccountRepository.save(mergedAccount);
                        logger.info("Updated/merged Account {} in local from cloud", entityAccountId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local Account {} is newer than cloud, no sync needed", accountId);
                    }
                } else {
                    // Local Account doesn't exist, create it
                    cloudAccount.setNew(true);
                    localAccountRepository.save(cloudAccount);
                    logger.info("Created Account {} in local from cloud (was UPDATE but not found)", accountId);
                }
            } else {
                logger.warn("Cloud account {} not found for UPDATE sync", accountId);
            }
        }
    }

}
