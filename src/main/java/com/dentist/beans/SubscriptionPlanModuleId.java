package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;

public class SubscriptionPlanModuleId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer subscriptionPlan;
    private Integer module;

    public SubscriptionPlanModuleId() {}
    
    public SubscriptionPlanModuleId(Integer subscriptionPlan, Integer module) {
        this.subscriptionPlan = subscriptionPlan;
        this.module = module;
    }

    public Integer getSubscriptionPlan() { 
        return subscriptionPlan; 
    }
    
    public void setSubscriptionPlan(Integer subscriptionPlan) { 
        this.subscriptionPlan = subscriptionPlan; 
    }
    
    public Integer getModule() { 
        return module; 
    }
    
    public void setModule(Integer module) { 
        this.module = module; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionPlanModuleId that = (SubscriptionPlanModuleId) o;
        return Objects.equals(subscriptionPlan, that.subscriptionPlan) && 
               Objects.equals(module, that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscriptionPlan, module);
    }
}
