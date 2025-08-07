package com.dentist.repository.local;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.dentist.beans.SyncQueueItem;

public interface LocalSyncQueueRepositoryJPA extends JpaRepository<SyncQueueItem, UUID> {
    
    /**
     * Find unprocessed sync queue items for the current account ordered by creation time
     */
    @Query("SELECT s FROM SyncQueueItem s WHERE s.processed = false AND s.accountId = :accountId ORDER BY s.createdAt ASC, s.orderNb ASC")
    List<SyncQueueItem> findUnprocessedItemsByAccountId(@Param("accountId") int accountId);
    
    /**
     * Find unprocessed sync queue items by entity type for the current account
     */
    @Query("SELECT s FROM SyncQueueItem s WHERE s.processed = false AND s.entityType = :entityType AND s.accountId = :accountId ORDER BY s.createdAt ASC, s.orderNb ASC")
    List<SyncQueueItem> findUnprocessedItemsByEntityTypeAndAccountId(@Param("entityType") String entityType, @Param("accountId") int accountId);
    
    /**
     * Find items that failed processing (retry count > 0) and are not processed for the current account
     */
    @Query("SELECT s FROM SyncQueueItem s WHERE s.processed = false AND s.retryCount > 0 AND s.accountId = :accountId ORDER BY s.createdAt ASC, s.orderNb ASC")
    List<SyncQueueItem> findFailedItemsByAccountId(@Param("accountId") int accountId);
    
    /**
     * Find items created after a specific timestamp for the current account
     */
    @Query("SELECT s FROM SyncQueueItem s WHERE s.createdAt > :timestamp AND s.accountId = :accountId ORDER BY s.createdAt ASC, s.orderNb ASC")
    List<SyncQueueItem> findItemsCreatedAfterByAccountId(@Param("timestamp") Instant timestamp, @Param("accountId") int accountId);
    
    /**
     * Mark a single item as processed
     */
    @Modifying
    @Transactional("localTransactionManager")
    @Query("UPDATE SyncQueueItem s SET s.processed = true, s.processedAt = :processedAt WHERE s.id = :id")
    void markItemAsProcessed(@Param("id") UUID id, @Param("processedAt") Instant processedAt);
    
    /**
     * Update retry count and error message for a sync item
     */
    @Modifying
    @Transactional("localTransactionManager")
    @Query("UPDATE SyncQueueItem s SET s.retryCount = s.retryCount + 1, s.errorMessage = :errorMessage WHERE s.id = :id")
    void incrementRetryCount(@Param("id") UUID id, @Param("errorMessage") String errorMessage);
    
    /**
     * Clean up old processed items for the current account
     */
    @Modifying
    @Transactional("localTransactionManager")
    @Query("DELETE FROM SyncQueueItem s WHERE s.processed = true AND s.processedAt < :cutoffDate AND s.accountId = :accountId")
    void deleteOldProcessedItemsByAccountId(@Param("cutoffDate") Instant cutoffDate, @Param("accountId") int accountId);
    
    /**
     * Get count of unprocessed items for monitoring
     */
    @Query("SELECT COUNT(s) FROM SyncQueueItem s WHERE s.processed = false AND s.accountId = :accountId")
    long countUnprocessedItemsByAccountId(@Param("accountId") int accountId);
}
