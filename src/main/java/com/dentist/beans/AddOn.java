package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "add_ons")
public class AddOn implements Serializable, Persistable<Integer>, ComparableSyncItem {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "code", unique = true)
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "add_on_type")
	private String addOnType; // 'module', 'quota_increase', 'quota_package'

	@Column(name = "price")
	private BigDecimal price;

	@Column(name = "currency")
	private String currency;

	@Column(name = "is_recurring")
	private Boolean isRecurring;

	@Column(name = "billing_period")
	private String billingPeriod; // 'one_time', 'monthly', 'yearly'

	@Column(name = "status")
	private int status;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@OneToMany(mappedBy = "addOn", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<AddOnModule> modules;

	@OneToMany(mappedBy = "addOn", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<AddOnQuotaEffect> quotaEffects;

	@Transient
	private boolean isNew = false;
	
	// Getters and setters
	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getAddOnType() {
		return addOnType;
	}

	public void setAddOnType(String addOnType) {
		this.addOnType = addOnType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Boolean getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public String getBillingPeriod() {
		return billingPeriod;
	}

	public void setBillingPeriod(String billingPeriod) {
		this.billingPeriod = billingPeriod;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public Set<AddOnModule> getModules() {
		return modules;
	}

	public void setModules(Set<AddOnModule> modules) {
		this.modules = modules;
	}

	public Set<AddOnQuotaEffect> getQuotaEffects() {
		return quotaEffects;
	}

	public void setQuotaEffects(Set<AddOnQuotaEffect> quotaEffects) {
		this.quotaEffects = quotaEffects;
	}
	
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	@Override
	public boolean isNew() {
		return isNew;
	}
}
