package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "notification_types")
public class NotificationType implements Serializable, Persistable<String>, ComparableSyncItem {

    private static final long serialVersionUID = -123456789012345679L;

    @Id
    @Column(name = "type_code", length = 50)
    private String typeCode;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "is_system", nullable = false)
    private boolean isSystem = false;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Transient
	private boolean isNew = false;
	
    public NotificationType() {
        super();
    }

    public NotificationType(String typeCode, String description, boolean isSystem) {
        this.typeCode = typeCode;
        this.description = description;
        this.isSystem = isSystem;
    }

    
    
    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean isSystem) {
        this.isSystem = isSystem;
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
	
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	@Override
	public boolean isNew() {
		return isNew;
	}

	@Override
	public String getId() {
		return this.typeCode;
	}
}
