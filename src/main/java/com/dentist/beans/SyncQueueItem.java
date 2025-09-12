package com.dentist.beans;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "sync_queue")
public class SyncQueueItem implements Persistable<UUID> {
    
    @Id
    @Column(name = "id")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;
    
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;
    
    @Column(name = "entity_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID entityId;
    
    @Column(name = "account_id", nullable = false)
    private int accountId;
    
    @Column(name = "change_type", nullable = false, length = 20)
    private String changeType;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "processed", nullable = false)
    private boolean processed = false;
    
    @Column(name = "processed_at")
    private Instant processedAt;
    
    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "order_nb", nullable = true)
    private Integer orderNb;

    @Transient
    private boolean isNew = false;
    
    // Constructors
    public SyncQueueItem() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
    
    public SyncQueueItem(String entityType, UUID entityId, int accountId, String changeType) {
        this();
        this.entityType = entityType;
        this.entityId = entityId;
        this.accountId = accountId;
        this.changeType = changeType;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public UUID getEntityId() {
        return entityId;
    }
    
    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public String getChangeType() {
        return changeType;
    }
    
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isProcessed() {
        return processed;
    }
    
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
    
    public Instant getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    // Helper methods
    public void markAsProcessed() {
        this.processed = true;
        this.processedAt = Instant.now();
    }
    
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    public Integer getOrderNb() {
		return orderNb;
	}

	public void setOrderNb(Integer orderNb) {
		this.orderNb = orderNb;
	}
    
    // Constants for entity types
    public static final String ENTITY_TYPE_INVOICE = "INVOICE";
    public static final String ENTITY_TYPE_INVOICE_ITEM = "INVOICE_ITEM";
    public static final String ENTITY_TYPE_INVOICE_ALLOCATION = "INVOICE_ALLOCATION";
    public static final String ENTITY_TYPE_PAYMENT = "PAYMENT";
    public static final String ENTITY_TYPE_CUSTOMER = "CUSTOMER";
    public static final String ENTITY_TYPE_APPOINTMENT = "APPOINTMENT";
    public static final String ENTITY_TYPE_TREATMENT = "TREATMENT";
    public static final String ENTITY_TYPE_OPERATION = "OPERATION";
    public static final String ENTITY_TYPE_CLASS_TYPE = "CLASS_TYPE";
    public static final String ENTITY_TYPE_MEDICAL_SHEET = "MEDICAL_SHEET";
    public static final String ENTITY_TYPE_PROCEDURE = "PROCEDURE";
    public static final String ENTITY_TYPE_ACCOUNT = "ACCOUNT";
    public static final String ENTITY_TYPE_RECALL = "RECALL";
    public static final String ENTITY_TYPE_CLINIC = "CLINIC";
    public static final String ENTITY_TYPE_CONFIG_ACCOUNT_SETTING = "CONFIG_ACCOUNT_SETTING";
    public static final String ENTITY_TYPE_CONFIG_CLINIC_SETTING = "CONFIG_CLINIC_SETTING";
    public static final String ENTITY_TYPE_USER = "USER";
    
    // Constants for change types
    public static final String CHANGE_TYPE_CREATE = "CREATE";
    public static final String CHANGE_TYPE_UPDATE = "UPDATE";
    public static final String CHANGE_TYPE_DELETE = "DELETE";

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
    
    @Override
    public boolean isNew() {
        return isNew;
    }
}
