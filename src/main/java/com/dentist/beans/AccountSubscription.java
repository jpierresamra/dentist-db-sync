package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "account_subscriptions")
public class AccountSubscription implements Serializable, Persistable<UUID>, ComparableSyncItem {
    
    private static final long serialVersionUID = 1L;

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private int accountId;

    @ManyToOne
    @JoinColumn(name = "sub_plan_id", referencedColumnName = "id")
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "billing_period")
    private String billingPeriod;

    @Column(name = "status")
    private int status;

    @Column(name = "auto_renew")
    private Boolean autoRenew;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;
    
    @Column(name = "update_date")
	private Date updateDate;

    @OneToMany(mappedBy = "accountSubscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccountSubscriptionModule> modules;

    @OneToMany(mappedBy = "accountSubscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AccountSubscriptionQuota> quotas;

    @Transient
	private boolean isNew = false;
    
    @Override
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    
    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }
    
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    
    public Boolean getAutoRenew() { return autoRenew; }
    public void setAutoRenew(Boolean autoRenew) { this.autoRenew = autoRenew; }
    
    
    public Set<AccountSubscriptionModule> getModules() { return modules; }
    public void setModules(Set<AccountSubscriptionModule> modules) { this.modules = modules; }
    
    public Set<AccountSubscriptionQuota> getQuotas() { return quotas; }
    public void setQuotas(Set<AccountSubscriptionQuota> quotas) { this.quotas = quotas; }
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
