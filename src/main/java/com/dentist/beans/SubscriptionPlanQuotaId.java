package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;

public class SubscriptionPlanQuotaId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer subscriptionPlan;
    private Integer quotaType;

    public SubscriptionPlanQuotaId() {}
    
    public SubscriptionPlanQuotaId(Integer subscriptionPlan, Integer quotaType) {
        this.subscriptionPlan = subscriptionPlan;
        this.quotaType = quotaType;
    }

    public Integer getSubscriptionPlan() { 
        return subscriptionPlan; 
    }
    
    public void setSubscriptionPlan(Integer subscriptionPlan) { 
        this.subscriptionPlan = subscriptionPlan; 
    }
    
    public Integer getQuotaType() { 
        return quotaType; 
    }
    
    public void setQuotaType(Integer quotaType) { 
        this.quotaType = quotaType; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionPlanQuotaId that = (SubscriptionPlanQuotaId) o;
        return Objects.equals(subscriptionPlan, that.subscriptionPlan) && 
               Objects.equals(quotaType, that.quotaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriptionPlan, quotaType);
    }
}
