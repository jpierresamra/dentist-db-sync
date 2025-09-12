package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "class_types")
public class ClassType implements Serializable, Persistable<UUID>, ComparableSyncItem {

    public static final int STATUS_CREATED = 1;
	public static final int STATUS_DELETED = 2;
	
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "color", nullable = false)
    private String color;
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "account_id", nullable = false)
    private int accountId;

    @Column(name = "create_date", updatable = false, insertable = true)
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;
    
    @Column(name = "status", nullable = false)
    private int status;

    @Transient
    private boolean isNew = false;

    public ClassType() {
        super();
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
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
	
    public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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
		return "Procedure [id=" + id + ", name=" + name + ", color=" + color + ", accountId=" + accountId + ", createDate=" + createDate + ", updateDate=" + updateDate + ", status=" + status + "]";
	}

}