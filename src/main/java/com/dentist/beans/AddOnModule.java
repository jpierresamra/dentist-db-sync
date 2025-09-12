package com.dentist.beans;

import jakarta.persistence.*;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "add_on_modules")
@IdClass(AddOnModuleId.class)
public class AddOnModule implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "add_on_id", referencedColumnName = "id")
    @JsonIgnore
    private AddOn addOn;

    @Id
    @ManyToOne
    @JoinColumn(name = "module_id", referencedColumnName = "id")
    private Module module;

    // Getters and setters
    public AddOn getAddOn() { 
        return addOn; 
    }
    
    public void setAddOn(AddOn addOn) { 
        this.addOn = addOn; 
    }
    
    public Module getModule() { 
        return module; 
    }
    
    public void setModule(Module module) { 
        this.module = module; 
    }
}
