package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "quota_types")
public class QuotaType implements Serializable, Persistable<Integer>, ComparableSyncItem {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "quota_key", unique = true)
	private String quotaKey;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "quota_category")
	private String quotaCategory; // 'max_limit', 'consumable'

	@Column(name = "unit")
	private String unit;

	@Column(name = "reset_period")
	private String resetPeriod; // 'never', 'monthly', 'yearly'

	@Column(name = "default_value")
	private java.math.BigDecimal defaultValue;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Transient
	private boolean isNew = false;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuotaKey() {
		return quotaKey;
	}

	public void setQuotaKey(String quotaKey) {
		this.quotaKey = quotaKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuotaCategory() {
		return quotaCategory;
	}

	public void setQuotaCategory(String quotaCategory) {
		this.quotaCategory = quotaCategory;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getResetPeriod() {
		return resetPeriod;
	}

	public void setResetPeriod(String resetPeriod) {
		this.resetPeriod = resetPeriod;
	}

	public java.math.BigDecimal getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(java.math.BigDecimal defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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
}
