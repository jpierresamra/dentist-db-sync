package com.dentist.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dentist.service.EventBasedSyncService;

@Component
@ConditionalOnProperty(name = "sync.event-based.enabled", havingValue = "true", matchIfMissing = true)
public class EventBasedSyncScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(EventBasedSyncScheduler.class);
    
    @Autowired
    private EventBasedSyncService eventBasedSyncService;
    
    /**
     * Process queued sync events every 30 seconds
     * This is much more efficient than the full table scan approach
     */
    @Scheduled(fixedDelay = 30000) // 30 seconds
    public void processQueuedSyncEvents() {
        try {
            long unprocessedCount = eventBasedSyncService.getUnprocessedItemCount();
            
            if (unprocessedCount > 0) {
                logger.info("Processing {} queued sync events", unprocessedCount);
                eventBasedSyncService.processQueuedSyncEvents();
            } else {
                logger.debug("No queued sync events to process");
            }
            
        } catch (Exception e) {
            logger.error("Error during scheduled sync event processing: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Process failed sync items (retry logic) every 5 minutes
     */
    @Scheduled(fixedDelay = 300000) // 5 minutes
    public void retryFailedSyncEvents() {
        try {
            // The processQueuedSyncEvents will also handle retries
            // This is just a separate schedule for failed items
            eventBasedSyncService.processQueuedSyncEvents();
            
        } catch (Exception e) {
            logger.error("Error during retry of failed sync events: {}", e.getMessage(), e);
        }
    }
}
