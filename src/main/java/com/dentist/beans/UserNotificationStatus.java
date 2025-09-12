package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_notification_status")
@IdClass(UserNotificationStatusId.class)
public class UserNotificationStatus implements Serializable {

    private static final long serialVersionUID = -123456789012345681L;

    @Id
    @Column(name = "notification_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID notificationId;

    @Id
    @Column(name = "user_id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID userId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "read_date", nullable = true)
    private Date readDate;

    @Column(name = "is_dismissed", nullable = false)
    private boolean isDismissed = false;

    @Column(name = "dismissed_date", nullable = true)
    private Date dismissedDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", referencedColumnName = "notification_id", insertable = false, updatable = false)
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    public UserNotificationStatus() {
        super();
        this.isRead = false;
        this.isDismissed = false;
    }

    public UserNotificationStatus(UUID notificationId, UUID userId) {
        this();
        this.notificationId = notificationId;
        this.userId = userId;
    }

    // Helper methods
    public void markAsRead() {
        this.isRead = true;
        this.readDate = new Date();
    }

    public void markAsDismissed() {
        this.isDismissed = true;
        this.dismissedDate = new Date();
    }

    public boolean isUnread() {
        return !isRead && !isDismissed;
    }

    // Getters and Setters
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

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    public boolean isDismissed() {
        return isDismissed;
    }

    public void setDismissed(boolean isDismissed) {
        this.isDismissed = isDismissed;
    }

    public Date getDismissedDate() {
        return dismissedDate;
    }

    public void setDismissedDate(Date dismissedDate) {
        this.dismissedDate = dismissedDate;
    }

    @JsonIgnore
    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
    
    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserNotificationStatus [notificationId=" + notificationId + ", userId=" + userId + 
               ", isRead=" + isRead + ", readDate=" + readDate + ", isDismissed=" + isDismissed + 
               ", dismissedDate=" + dismissedDate + "]";
    }
}
