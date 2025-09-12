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
@Table(name = "add_on_quotas")
@IdClass(AddOnQuotaId.class)
public class AddOnQuota implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "add_on_id", referencedColumnName = "id")
    @JsonIgnore
    private AddOn addOn;

    @Id
    @Column(name = "quota_key")
    private String quotaKey; // e.g. "max_storage_gb", "sms_monthly_limit"

    @Column(name = "quota_change_value")
    private BigDecimal quotaChangeValue; // Amount to add/increase quota by

    @Column(name = "quota_unit")
    private String quotaUnit; // e.g. "GB", "messages"

    @Column(name = "change_type")
    private String changeType; // "increase", "replenish", "enable_feature"

    // Getters and setters
    public AddOn getAddOn() { 
        return addOn; 
    }
    
    public void setAddOn(AddOn addOn) { 
        this.addOn = addOn; 
    }
    
    public String getQuotaKey() { 
        return quotaKey; 
    }
    
    public void setQuotaKey(String quotaKey) { 
        this.quotaKey = quotaKey; 
    }
    
    public BigDecimal getQuotaChangeValue() { 
        return quotaChangeValue; 
    }
    
    public void setQuotaChangeValue(BigDecimal quotaChangeValue) { 
        this.quotaChangeValue = quotaChangeValue; 
    }
    
    public String getQuotaUnit() { 
        return quotaUnit; 
    }
    
    public void setQuotaUnit(String quotaUnit) { 
        this.quotaUnit = quotaUnit; 
    }
    
    public String getChangeType() { 
        return changeType; 
    }
    
    public void setChangeType(String changeType) { 
        this.changeType = changeType; 
    }
}
