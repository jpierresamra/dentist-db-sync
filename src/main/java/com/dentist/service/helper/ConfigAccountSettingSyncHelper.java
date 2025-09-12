package com.dentist.service.helper;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dentist.beans.ConfigAccountSetting;
import com.dentist.beans.SyncQueueItem;
import com.dentist.enums.ChangeType;
import com.dentist.repository.cloud.CloudConfigAccountSettingsRepositoryJPA;
import com.dentist.repository.local.LocalConfigAccountSettingsRepositoryJPA;
import com.dentist.util.SyncUtil;

/**
 * Helper class for ConfigSetting entity synchronization between local and cloud databases.
 * Provides smart merge capabilities with field-level conflict resolution and account-based filtering.
 * 
 * Features:
 * - Account-based multi-tenant security
 * - Smart merge with comprehensive field handling
 * - ConfigSetting-specific logic (amount, method, currency)
 * - Enhanced logging and error handling
 * 
 * @author Generated Helper Pattern
 * @version 1.0
 */
@Component
public class ConfigAccountSettingSyncHelper {

    private static final Logger logger = LoggerFactory.getLogger(ConfigAccountSettingSyncHelper.class);
    
    private final CloudConfigAccountSettingsRepositoryJPA cloudConfigAccountSettingsRepository;
    private final LocalConfigAccountSettingsRepositoryJPA localConfigAccountSettingsRepository;
    
    @Value("${account.id}")
    private int accountId;
    
    @Value("${sync.merge.enabled:true}")
    private boolean smartMergeEnabled;
    
    @Value("${sync.merge.time-threshold-minutes:5}")
    private int mergeTimeThresholdMinutes;

    public ConfigAccountSettingSyncHelper(
    		CloudConfigAccountSettingsRepositoryJPA cloudConfigAccountSettingsRepository,
    		LocalConfigAccountSettingsRepositoryJPA localConfigAccountSettingsRepository) {
        this.cloudConfigAccountSettingsRepository = cloudConfigAccountSettingsRepository;
        this.localConfigAccountSettingsRepository = localConfigAccountSettingsRepository;
    }

    /**
     * Process a ConfigSetting sync item with smart merge capabilities
     * @param syncItem the sync queue item
     * @param localToCloud true if syncing from local to cloud, false if cloud to local
     * @throws Exception if sync fails
     */
    public void processConfigSettingSyncItem(SyncQueueItem syncItem, boolean localToCloud) throws Exception {
        ChangeType changeType = ChangeType.valueOf(syncItem.getChangeType());
        
        switch (changeType) {
            case UPDATE:
                syncConfigSettingUpdate(syncItem.getEntityId(), localToCloud);
                break;
            default:
                throw new IllegalArgumentException("Unknown change type: " + syncItem.getChangeType());
        }
    }

    /**
     * Handles UPDATE sync operations for ConfigSettings with intelligent conflict resolution.
     */
    private void syncConfigSettingUpdate(UUID ConfigSettingId, boolean localToCloud) throws Exception {
        if (localToCloud) {
            // Sync from local to cloud
            Optional<ConfigAccountSetting> localConfigSettingOpt = localConfigAccountSettingsRepository.findByIdAndAccountId(ConfigSettingId, accountId);
            
            if (localConfigSettingOpt.isPresent()) {
            	ConfigAccountSetting localConfigSetting = localConfigSettingOpt.get();
                Optional<ConfigAccountSetting> cloudConfigSettingOpt = cloudConfigAccountSettingsRepository.findByIdAndAccountId(ConfigSettingId, accountId);
                
                if (cloudConfigSettingOpt.isPresent()) {
                    ConfigAccountSetting cloudConfigSetting = cloudConfigSettingOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                    ConfigAccountSetting mergedConfigSetting = (ConfigAccountSetting) SyncUtil.performSimpleTimestampMerge(localConfigSetting, cloudConfigSetting, true);
                    
                    if (mergedConfigSetting != null) {
                        // Changes were merged or local is newer
                        cloudConfigAccountSettingsRepository.save(mergedConfigSetting);
                        logger.info("Updated/merged ConfigSetting {} in cloud", ConfigSettingId);
                    } else {
                        // Cloud is newer, no local-to-cloud sync needed
                        logger.debug("Cloud ConfigSetting {} is newer than local, no sync needed", ConfigSettingId);
                    }
                } else {
                    // Cloud ConfigSetting doesn't exist, create it
                    localConfigSetting.setNew(true);
                    cloudConfigAccountSettingsRepository.save(localConfigSetting);
                    logger.info("Created ConfigSetting {} in cloud (was UPDATE but not found)", ConfigSettingId);
                }
            } else {
                logger.warn("Local ConfigSetting {} not found for UPDATE sync", ConfigSettingId);
            }
        } else {
            // Sync from cloud to local
            Optional<ConfigAccountSetting> cloudConfigSettingOpt = cloudConfigAccountSettingsRepository.findByIdAndAccountId(ConfigSettingId, accountId);
            
            if (cloudConfigSettingOpt.isPresent()) {
                ConfigAccountSetting cloudConfigAccountSetting = cloudConfigSettingOpt.get();
                Optional<ConfigAccountSetting> localConfigSettingOpt = localConfigAccountSettingsRepository.findByIdAndAccountId(ConfigSettingId, accountId);
                
                if (localConfigSettingOpt.isPresent()) {
                	ConfigAccountSetting localConfigSetting = localConfigSettingOpt.get();
                    
                    // Smart merge: Handle concurrent updates to different fields
                	ConfigAccountSetting mergedConfigSetting = (ConfigAccountSetting) SyncUtil.performSimpleTimestampMerge(cloudConfigAccountSetting, localConfigSetting, false);
                    
                    if (mergedConfigSetting != null) {
                        // Changes were merged or cloud is newer
                    	localConfigAccountSettingsRepository.save(mergedConfigSetting);
                        logger.info("Updated/merged ConfigSetting {} in local from cloud", ConfigSettingId);
                    } else {
                        // Local is newer, no cloud-to-local sync needed
                        logger.debug("Local ConfigSetting {} is newer than cloud, no sync needed", ConfigSettingId);
                    }
                } else {
                    // Local ConfigSetting doesn't exist, create it
                    cloudConfigAccountSetting.setNew(true);
                    localConfigAccountSettingsRepository.save(cloudConfigAccountSetting);
                    logger.info("Created ConfigSetting {} in local from cloud (was UPDATE but not found)", ConfigSettingId);
                }
            } else {
                logger.warn("Cloud ConfigSetting {} not found for UPDATE sync for account {}", ConfigSettingId, accountId);
            }
        }
    }
}
