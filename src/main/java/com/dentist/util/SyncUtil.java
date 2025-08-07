package com.dentist.util;

import java.util.Date;

import com.dentist.beans.ComparableSyncItem;

public class SyncUtil {

	public static ComparableSyncItem performSimpleTimestampMerge(
			ComparableSyncItem sourceComparableItem, 
			ComparableSyncItem targetComparableItem, 
			boolean sourceToTarget) {
        Date sourceUpdate = sourceComparableItem.getUpdateDate();
        Date targetUpdate = targetComparableItem.getUpdateDate();
        
        //If both update dates are empty (which shouldn't be the case), then return the source since the source has the change queue
        if (sourceUpdate == null && targetUpdate == null) {
            return null;
        }
        
        //If the sourceUpdate is null but the target Update is not (which shouldn't be the case) because the source target is flagged as updated
        // then return null, And leave the sync to happen the other way around
        // TODO, here maybe we should flag an inconsistency and let the user manually resolve it???
        if (sourceUpdate == null) {
            return null;
        }
        
        //normal case for the very first change on the source where target update is still null, then return the source
        if (targetUpdate == null) {
            return sourceComparableItem;
        }
        
        if (sourceUpdate.after(targetUpdate)) {
            return sourceComparableItem;
        } 
        // If the target update date is more recent then wait for the sync to fire from target for the update
        // TODO, here maybe we should flag an inconsistency and let the user manually resolve it???
        else {
            return null;
        } 
    }
}
