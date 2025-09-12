package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;

public class AddOnQuotaEffectId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer addOn;
    private Integer quotaType;

    public AddOnQuotaEffectId() {}
    
    public AddOnQuotaEffectId(Integer addOn, Integer quotaType) {
        this.addOn = addOn;
        this.quotaType = quotaType;
    }

    public Integer getAddOn() { 
        return addOn; 
    }
    
    public void setAddOn(Integer addOn) { 
        this.addOn = addOn; 
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
        AddOnQuotaEffectId that = (AddOnQuotaEffectId) o;
        return Objects.equals(addOn, that.addOn) && 
               Objects.equals(quotaType, that.quotaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addOn, quotaType);
    }
}
