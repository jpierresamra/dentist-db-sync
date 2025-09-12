package com.dentist.beans;

import java.io.Serializable;
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
@Table(name = "config_clinic_settings")
public class ConfigClinicSetting implements Serializable, Persistable<UUID>, ComparableSyncItem {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "setting_key", nullable = false, length = 100)
    private String settingKey;

    @Column(name = "setting_value", nullable = false, length = 500)
    private String settingValue;

    @Column(name = "setting_type", nullable = false, length = 50)
    private String settingType = "string";

    @Column(name = "description", length = 255)
    private String description;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

    @Column(name = "clinic_id", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID clinicId;
    
    @Column(name = "account_id", nullable = false)
    private int accountId;

	@Transient
	private boolean isNew = false;
	
    // Default constructor
    public ConfigClinicSetting() {}

    // Constructor with key and value
    public ConfigClinicSetting(String settingKey, String settingValue, int accountId) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.accountId = accountId;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getSettingType() {
        return settingType;
    }

    public void setSettingType(String settingType) {
        this.settingType = settingType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

	public UUID getClinicId() {
		return clinicId;
	}

	public void setClinicId(UUID clinicId) {
		this.clinicId = clinicId;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	@Override
	public boolean isNew() {
		return isNew;
	}
	
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
    public String toString() {
		return "ConfigAccountSetting [id=" + id + ", settingKey=" + settingKey + ", settingValue=" + settingValue
				+ ", settingType=" + settingType + ", description=" + description + ", updateDate=" + updateDate
				+ ", accountId=" + accountId + "]";
    }
}
