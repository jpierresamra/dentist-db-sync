package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscription_plan_quotas")
@IdClass(SubscriptionPlanQuotaId.class)
public class SubscriptionPlanQuota implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "sub_plan_id", referencedColumnName = "id")
    @JsonIgnore
    private SubscriptionPlan subscriptionPlan;

    @Id
    @ManyToOne
    @JoinColumn(name = "quota_type_id", referencedColumnName = "id")
    private QuotaType quotaType;

    @Column(name = "quota_value")
    private BigDecimal quotaValue;

    @Column(name = "is_unlimited")
    private Boolean isUnlimited;

    // Getters and setters
    public SubscriptionPlan getSubscriptionPlan() { 
        return subscriptionPlan; 
    }
    
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { 
        this.subscriptionPlan = subscriptionPlan; 
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
    
    public Boolean getIsUnlimited() { 
        return isUnlimited; 
    }
    
    public void setIsUnlimited(Boolean isUnlimited) { 
        this.isUnlimited = isUnlimited; 
    }
}
