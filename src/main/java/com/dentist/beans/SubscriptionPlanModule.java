package com.dentist.beans;

import java.io.Serializable;
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
@Table(name = "subscription_plan_modules")
@IdClass(SubscriptionPlanModuleId.class)
public class SubscriptionPlanModule implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "sub_plan_id", referencedColumnName = "id")
    @JsonIgnore
    private SubscriptionPlan subscriptionPlan;

    @Id
    @ManyToOne
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private Module module;

    @Column(name = "is_included")
    private Boolean isIncluded;

    @Column(name = "added_date")
    private Date addedDate;

    // Getters and setters
    public SubscriptionPlan getSubscriptionPlan() { 
        return subscriptionPlan; 
    }
    
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { 
        this.subscriptionPlan = subscriptionPlan; 
    }
    
    public Module getModule() { 
        return module; 
    }
    
    public void setModule(Module module) { 
        this.module = module; 
    }
    
    public Boolean getIsIncluded() { 
        return isIncluded; 
    }
    
    public void setIsIncluded(Boolean isIncluded) { 
        this.isIncluded = isIncluded; 
    }
    
    public Date getAddedDate() { 
        return addedDate; 
    }
    
    public void setAddedDate(Date addedDate) { 
        this.addedDate = addedDate; 
    }
}
