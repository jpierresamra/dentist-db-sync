package com.dentist.util;

import java.util.UUID;

/**
 * Utility class for Account entity sync operations
 * Handles conversion between int accountId and UUID for sync consistency
 */
public class AccountSyncUtil {
    
    /**
     * Converts an int accountId to a deterministic UUID for sync operations
     * @param accountId the integer account ID
     * @return UUID representation of the account ID
     */
    public static UUID accountIdToUUID(int accountId) {
        return UUID.nameUUIDFromBytes(String.valueOf(accountId).getBytes());
    }
    
    /**
     * Extracts the original int accountId from a UUID created by accountIdToUUID
     * This works by reverse-engineering the deterministic UUID generation
     * 
     * Note: This is a helper method, but in practice we can use the accountId 
     * field from SyncQueueItem directly since it's already stored there
     * 
     * @param uuid the UUID created from an account ID
     * @return the original integer account ID
     */
    public static int uuidToAccountId(UUID uuid) {
        // Since we can't reliably reverse UUID.nameUUIDFromBytes(), 
        // this method serves as documentation of the relationship
        // In practice, use SyncQueueItem.getAccountId() directly
        throw new UnsupportedOperationException("Use SyncQueueItem.getAccountId() directly instead");
    }
    
    /**
     * Validates that a UUID was created from an account ID
     * @param uuid the UUID to validate
     * @param expectedAccountId the expected account ID
     * @return true if the UUID matches the expected account ID
     */
    public static boolean validateAccountUUID(UUID uuid, int expectedAccountId) {
        return accountIdToUUID(expectedAccountId).equals(uuid);
    }
}
