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
@Table(name = "add_on_quota_effects")
@IdClass(AddOnQuotaEffectId.class)
public class AddOnQuotaEffect implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "add_on_id", referencedColumnName = "id")
    @JsonIgnore
    private AddOn addOn;

    @Id
    @ManyToOne
    @JoinColumn(name = "quota_type_id", referencedColumnName = "id")
    private QuotaType quotaType;

    @Column(name = "effect_type")
    private String effectType; // 'increase', 'add_package'

    @Column(name = "effect_value")
    private BigDecimal effectValue;

    // Getters and setters
    public AddOn getAddOn() { 
        return addOn; 
    }
    
    public void setAddOn(AddOn addOn) { 
        this.addOn = addOn; 
    }
    
    public QuotaType getQuotaType() { 
        return quotaType; 
    }
    
    public void setQuotaType(QuotaType quotaType) { 
        this.quotaType = quotaType; 
    }
    
    public String getEffectType() { 
        return effectType; 
    }
    
    public void setEffectType(String effectType) { 
        this.effectType = effectType; 
    }
    
    public BigDecimal getEffectValue() { 
        return effectValue; 
    }
    
    public void setEffectValue(BigDecimal effectValue) { 
        this.effectValue = effectValue; 
    }
}
