package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;

public class AddOnQuotaId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer addOn;
    private String quotaKey;

    public AddOnQuotaId() {}
    
    public AddOnQuotaId(Integer addOn, String quotaKey) {
        this.addOn = addOn;
        this.quotaKey = quotaKey;
    }

    public Integer getAddOn() { 
        return addOn; 
    }
    
    public void setAddOn(Integer addOn) { 
        this.addOn = addOn; 
    }
    
    public String getQuotaKey() { 
        return quotaKey; 
    }
    
    public void setQuotaKey(String quotaKey) { 
        this.quotaKey = quotaKey; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddOnQuotaId that = (AddOnQuotaId) o;
        return Objects.equals(addOn, that.addOn) && 
               Objects.equals(quotaKey, that.quotaKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addOn, quotaKey);
    }
}
