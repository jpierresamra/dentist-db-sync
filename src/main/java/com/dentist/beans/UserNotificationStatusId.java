package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserNotificationStatusId implements Serializable {

    private static final long serialVersionUID = -123456789012345682L;

    private UUID notificationId;
    private UUID userId;

    public UserNotificationStatusId() {
        super();
    }

    public UserNotificationStatusId(UUID notificationId, UUID userId) {
        this.notificationId = notificationId;
        this.userId = userId;
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNotificationStatusId that = (UserNotificationStatusId) o;
        return Objects.equals(notificationId, that.notificationId) &&
               Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, userId);
    }

    @Override
    public String toString() {
        return "UserNotificationStatusId [notificationId=" + notificationId + ", userId=" + userId + "]";
    }
}
