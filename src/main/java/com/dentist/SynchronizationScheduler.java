package com.dentist;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dentist.service.DBSyncService;

@Component
public class SynchronizationScheduler {

    private final DBSyncService dbSyncService;

    public SynchronizationScheduler(DBSyncService dbSyncService) {
        this.dbSyncService = dbSyncService;
    }

    @Scheduled(fixedRate = 30000) // Run every 60 seconds
    public void syncData() {
    	dbSyncService.synchronizeData();
    }
}