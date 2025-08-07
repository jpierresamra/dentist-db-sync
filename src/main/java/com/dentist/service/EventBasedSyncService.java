package com.dentist.service;

/**
 * Service interface for event-based database synchronization
 * This service processes queued sync events rather than doing full table scans
 */
public interface EventBasedSyncService {
    
    /**
     * Process all queued sync events for the current account
     */
    void processQueuedSyncEvents();
    
    /**
     * Get the count of unprocessed sync items for monitoring purposes
     */
    long getUnprocessedItemCount();
}
