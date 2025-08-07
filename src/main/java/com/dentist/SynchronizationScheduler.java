package com.dentist;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dentist.service.EventBasedSyncService;
import com.dentist.service.helper.CustomerSyncHelper;

import jakarta.annotation.PreDestroy;

@Component
public class SynchronizationScheduler {

    private final CustomerSyncHelper customerSyncHelper;

    private final EventBasedSyncService eventBasedSyncService;

    public SynchronizationScheduler(EventBasedSyncService eventBasedSyncService, CustomerSyncHelper customerSyncHelper) {
        this.eventBasedSyncService = eventBasedSyncService;
        this.customerSyncHelper = customerSyncHelper;
    }

    @Scheduled(fixedRate = 15000) // Run every 30 seconds
    public void syncData() {
    	eventBasedSyncService.processQueuedSyncEvents();
    	System.out.println("SynchronizationScheduler: Running scheduled sync task");
    }

    @PreDestroy
    public void onShutdown() {
        System.out.println("SynchronizationScheduler: Server is shutting down, running final sync...");
        try {
            eventBasedSyncService.processQueuedSyncEvents();
            System.out.println("SynchronizationScheduler: Final sync completed successfully");
        } catch (Exception e) {
            System.err.println("SynchronizationScheduler: Error during final sync: " + e.getMessage());
            e.printStackTrace();
        }
    }
}