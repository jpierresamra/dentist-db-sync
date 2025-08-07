package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.SyncQueueItem;
import com.dentist.beans.User;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudUserRepositoryJPA;
import com.dentist.repository.local.LocalUserRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for handling User entity synchronization between local and cloud databases.
 * 
 * This class encapsulates all User-specific sync logic including:
 * - CREATE/UPDATE/DELETE operations
 * - Intelligent conflict resolution with field-level merging
 * - Smart merge capabilities for concurrent updates
 * - Account-based filtering for multi-tenant security
 * 
 * Extracted from EventBasedSyncServiceImpl to improve code organization and maintainability.
 */
@Component
public class UserSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserSyncHelper.class);

    private final CloudUserRepositoryJPA cloudUserRepository;
    private final LocalUserRepositoryJPA localUserRepository;

    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public UserSyncHelper(CloudUserRepositoryJPA cloudUserRepository,
                            LocalUserRepositoryJPA localUserRepository) {
        this.cloudUserRepository = cloudUserRepository;
        this.localUserRepository = localUserRepository;
    }

    /**
     * Processes a User sync item based on the change type.
     */
    public void processUserSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case CREATE:
                syncUserCreate(syncItem.getEntityId(), localToCloud);
                break;
            case UPDATE:
                syncUserUpdate(syncItem.getEntityId(), localToCloud);
                break;
            case DELETE:
                syncUserDelete(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles CREATE sync operations for Users.
     */
    private void syncUserCreate(UUID UserId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<User> localUserOpt = localUserRepository.findByIdAndAccountId(UserId, accountId);
            
            if (localUserOpt.isPresent()) {
                User localUser = localUserOpt.get();
                
                // Check if User already exists in cloud
                Optional<User> cloudUserOpt = cloudUserRepository.findByIdAndAccountId(UserId, accountId);
                
                if (cloudUserOpt.isEmpty()) {
                    // Create in cloud
                    localUser.setNew(true);
                    cloudUserRepository.save(localUser);
                    logger.info("Created User {} in cloud", UserId);
                } else {
                    // Already exists, treat as update
                    syncUserUpdate(UserId, localToCloud);
                }
            } else {
                logger.warn("Local User {} not found for CREATE sync", UserId);
            }
        } else {
            // Sync from cloud to local
            Optional<User> cloudUserOpt = cloudUserRepository.findByIdAndAccountId(UserId, accountId);
            
            if (cloudUserOpt.isPresent()) {
                User cloudUser = cloudUserOpt.get();
                
                // Check if User already exists locally
                Optional<User> localUserOpt = localUserRepository.findByIdAndAccountId(UserId, accountId);
                
                if (localUserOpt.isEmpty()) {
                    // Create in local
                    cloudUser.setNew(true);
                    localUserRepository.save(cloudUser);
                    logger.info("Created User {} in local from cloud", UserId);
                } else {
                    // Already exists, treat as update
                    syncUserUpdate(UserId, localToCloud);
                }
            } else {
                logger.warn("Cloud User {} not found for CREATE sync for account {}", UserId, accountId);
            }
        }
    }

    /**
     * Handles UPDATE sync operations for Users with intelligent conflict resolution.
     */
    private void syncUserUpdate(UUID UserId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<User> localUserOpt = localUserRepository.findByIdAndAccountId(UserId, accountId);
            
            if (localUserOpt.isPresent()) {
                User localUser = localUserOpt.get();
                Optional<User> cloudUserOpt = cloudUserRepository.findByIdAndAccountId(UserId, accountId);
                
                if (cloudUserOpt.isPresent()) {
                    User cloudUser = cloudUserOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    User mergedUser = (User) SyncUtil.performSimpleTimestampMerge(localUser, cloudUser, true);
                    
                    if (mergedUser != null) {
                        // Changes were merged or local is newer
                        cloudUserRepository.save(mergedUser);
                        logger.info("Updated/merged User {} in cloud", UserId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud User {} is newer than local, no sync needed", UserId);
                    }
                } else {
                    // Cloud User doesn't exist, create it
                    localUser.setNew(true);
                    cloudUserRepository.save(localUser);
                    logger.info("Created User {} in cloud (was UPDATE but not found)", UserId);
                }
            } else {
                logger.warn("Local User {} not found for UPDATE sync", UserId);
            }
        } else {
            // Sync from cloud to local
            Optional<User> cloudUserOpt = cloudUserRepository.findByIdAndAccountId(UserId, accountId);
            
            if (cloudUserOpt.isPresent()) {
                User cloudUser = cloudUserOpt.get();
                Optional<User> localUserOpt = localUserRepository.findByIdAndAccountId(UserId, accountId);
                
                if (localUserOpt.isPresent()) {
                    User localUser = localUserOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    User mergedUser = (User) SyncUtil.performSimpleTimestampMerge(cloudUser, localUser, false);
                    
                    if (mergedUser != null) {
                        // Changes were merged or cloud is newer
                        localUserRepository.save(mergedUser);
                        logger.info("Updated/merged User {} in local from cloud", UserId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local User {} is newer than cloud, no sync needed", UserId);
                    }
                } else {
                    // Local User doesn't exist, create it
                    cloudUser.setNew(true);
                    localUserRepository.save(cloudUser);
                    logger.info("Created User {} in local from cloud (was UPDATE but not found)", UserId);
                }
            } else {
                logger.warn("Cloud User {} not found for UPDATE sync for account {}", UserId, accountId);
            }
        }
    }

    /**
     * Handles DELETE sync operations for Users.
     */
    private void syncUserDelete(UUID UserId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud - mark as deleted in cloud
            Optional<User> cloudUserOpt = cloudUserRepository.findByIdAndAccountId(UserId, accountId);
            
            if (cloudUserOpt.isPresent()) {
                User cloudUser = cloudUserOpt.get();
                cloudUser.setStatus(User.STATUS_DELETED);
                cloudUserRepository.save(cloudUser);
                logger.info("Marked User {} as deleted in cloud", UserId);
            } else {
                logger.info("User {} not found in cloud for DELETE sync (already deleted?)", UserId);
            }
        } else {
            // Sync from cloud to local - mark as deleted in local
            Optional<User> localUserOpt = localUserRepository.findByIdAndAccountId(UserId, accountId);
            
            if (localUserOpt.isPresent()) {
                User localUser = localUserOpt.get();
                localUser.setStatus(User.STATUS_DELETED);
                localUserRepository.save(localUser);
                logger.info("Marked User {} as deleted in local from cloud", UserId);
            } else {
                logger.info("User {} not found in local for DELETE sync (already deleted?)", UserId);
            }
        }
    }
}
