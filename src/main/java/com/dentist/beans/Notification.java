package com.dentist.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "notifications")
public class Notification implements Serializable, Persistable<UUID>, ComparableSyncItem {

    private static final long serialVersionUID = -123456789012345680L;
    
    // Priority constants
    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_CRITICAL = "CRITICAL";
    
    // Scope constants
    public static final String SCOPE_SYSTEM = "SYSTEM";
    public static final String SCOPE_ACCOUNT = "ACCOUNT";
    public static final String SCOPE_USER = "USER";

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "notification_id")
    private UUID notificationId;

    @Column(name = "account_id", nullable = true)
    private Integer accountId;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "priority", nullable = false, length = 10)
    private String priority = PRIORITY_MEDIUM;

    @Column(name = "scope", nullable = false, length = 10)
    private String scope = SCOPE_ACCOUNT;

    @Column(name = "target_user_id", nullable = true)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID targetUserId;

    @Column(name = "target_roles", columnDefinition = "JSON")
    private String targetRoles;

    @Column(name = "created_by", nullable = true)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID createdBy;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

    @Column(name = "expires_date", nullable = true)
    private Date expiresDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_type", referencedColumnName = "type_code", insertable = false, updatable = false)
    private NotificationType notificationTypeEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User targetUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User createdByUser;

    @OneToMany(mappedBy = "notification", fetch = FetchType.EAGER)
    private List<UserNotificationStatus> userNotificationStatuses;

    @Transient
	private boolean isNew = false;
    
    public Notification() {
        super();
        this.priority = PRIORITY_MEDIUM;
        this.scope = SCOPE_ACCOUNT;
        this.isActive = true;
    }

    // Helper methods
    public String getCreatedDateString() {
        if (createDate == null) return "";
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sf.format(this.createDate);
    }

    public String getExpiresDateString() {
        if (expiresDate == null) return "";
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sf.format(this.expiresDate);
    }

    public boolean isExpired() {
        return expiresDate != null && expiresDate.before(new Date());
    }

    public boolean isSystemNotification() {
        return SCOPE_SYSTEM.equals(this.scope);
    }

    public boolean isAccountNotification() {
        return SCOPE_ACCOUNT.equals(this.scope);
    }

    public boolean isUserNotification() {
        return SCOPE_USER.equals(this.scope);
    }

    public boolean isCritical() {
        return PRIORITY_CRITICAL.equals(this.priority);
    }

    public boolean isHigh() {
        return PRIORITY_HIGH.equals(this.priority);
    }

    // Getters and Setters
    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public UUID getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(UUID targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetRoles() {
        return targetRoles;
    }

    public void setTargetRoles(String targetRoles) {
        this.targetRoles = targetRoles;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }


    public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getExpiresDate() {
        return expiresDate;
    }

    public void setExpiresDate(Date expiresDate) {
        this.expiresDate = expiresDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public NotificationType getNotificationTypeEntity() {
        return notificationTypeEntity;
    }

    public void setNotificationTypeEntity(NotificationType notificationTypeEntity) {
        this.notificationTypeEntity = notificationTypeEntity;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public List<UserNotificationStatus> getUserNotificationStatuses() {
        return userNotificationStatuses;
    }

    public void setUserNotificationStatuses(List<UserNotificationStatus> userNotificationStatuses) {
        this.userNotificationStatuses = userNotificationStatuses;
    }

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	@Override
	public boolean isNew() {
		return isNew;
	}
	
    @Override
    public String toString() {
        return "Notification [notificationId=" + notificationId + ", accountId=" + accountId + 
               ", notificationType=" + notificationType + ", title=" + title + ", priority=" + priority + 
               ", scope=" + scope + ", targetUserId=" + targetUserId + ", createdBy=" + createdBy + 
               ", createDate=" + createDate + ", isActive=" + isActive + "]";
    }

	@Override
	public UUID getId() {
		return this.getNotificationId();
	}
}
