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
@Table(name = "account_subscription_modules")
@IdClass(AccountSubscriptionModuleId.class)
public class AccountSubscriptionModule implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "account_subscription_id", referencedColumnName = "id")
    @JsonIgnore
    private AccountSubscription accountSubscription;

    @Id
    @ManyToOne
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private Module module;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "added_via")
    private String addedVia; // 'plan', 'add_on', 'custom'

    @Column(name = "added_date")
    private Date addedDate;

    // Getters and setters
    public AccountSubscription getAccountSubscription() { 
        return accountSubscription; 
    }
    
    public void setAccountSubscription(AccountSubscription accountSubscription) { 
        this.accountSubscription = accountSubscription; 
    }
    
    public Module getModule() { 
        return module; 
    }
    
    public void setModule(Module module) { 
        this.module = module; 
    }
    
    public Boolean getIsEnabled() { 
        return isEnabled; 
    }
    
    public void setIsEnabled(Boolean isEnabled) { 
        this.isEnabled = isEnabled; 
    }
    
    public String getAddedVia() { 
        return addedVia; 
    }
    
    public void setAddedVia(String addedVia) { 
        this.addedVia = addedVia; 
    }
    
    public Date getAddedDate() { 
        return addedDate; 
    }
    
    public void setAddedDate(Date addedDate) { 
        this.addedDate = addedDate; 
    }
}
