package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;

public class AddOnModuleId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer addOn;
    private Integer module;

    public AddOnModuleId() {}
    
    public AddOnModuleId(Integer addOn, Integer module) {
        this.addOn = addOn;
        this.module = module;
    }

    public Integer getAddOn() { 
        return addOn; 
    }
    
    public void setAddOn(Integer addOn) { 
        this.addOn = addOn; 
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
        AddOnModuleId that = (AddOnModuleId) o;
        return Objects.equals(addOn, that.addOn) && 
               Objects.equals(module, that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addOn, module);
    }
}
