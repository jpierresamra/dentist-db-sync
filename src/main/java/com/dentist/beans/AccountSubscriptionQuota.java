package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "account_subscription_quotas")
@IdClass(AccountSubscriptionQuotaId.class)
public class AccountSubscriptionQuota implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "account_subscription_id", referencedColumnName = "id")
    @JsonIgnore
    private AccountSubscription accountSubscription;

    @Id
    @ManyToOne
    @JoinColumn(name = "quota_type_id", referencedColumnName = "id")
    private QuotaType quotaType;

    @Column(name = "quota_value")
    private BigDecimal quotaValue; // Current quota value (can be modified from plan default)

    @Column(name = "quota_used")
    private BigDecimal quotaUsed; // Currently used amount

    @Column(name = "is_unlimited")
    private Boolean isUnlimited;

    @Column(name = "last_reset_date")
    private Date lastResetDate; // When quota was last reset

    @Column(name = "next_reset_date")
    private Date nextResetDate; // When quota will next reset

    // Getters and setters
    public AccountSubscription getAccountSubscription() { 
        return accountSubscription; 
    }
    
    public void setAccountSubscription(AccountSubscription accountSubscription) { 
        this.accountSubscription = accountSubscription; 
    }
    
    public QuotaType getQuotaType() { 
        return quotaType; 
    }
    
    public void setQuotaType(QuotaType quotaType) { 
        this.quotaType = quotaType; 
    }
    
    public BigDecimal getQuotaValue() { 
        return quotaValue; 
    }
    
    public void setQuotaValue(BigDecimal quotaValue) { 
        this.quotaValue = quotaValue; 
    }
    
    public BigDecimal getQuotaUsed() { 
        return quotaUsed; 
    }
    
    public void setQuotaUsed(BigDecimal quotaUsed) { 
        this.quotaUsed = quotaUsed; 
    }
    
    public Boolean getIsUnlimited() { 
        return isUnlimited; 
    }
    
    public void setIsUnlimited(Boolean isUnlimited) { 
        this.isUnlimited = isUnlimited; 
    }
    
    public Date getLastResetDate() { 
        return lastResetDate; 
    }
    
    public void setLastResetDate(Date lastResetDate) { 
        this.lastResetDate = lastResetDate; 
    }
    
    public Date getNextResetDate() { 
        return nextResetDate; 
    }
    
    public void setNextResetDate(Date nextResetDate) { 
        this.nextResetDate = nextResetDate; 
    }
}
