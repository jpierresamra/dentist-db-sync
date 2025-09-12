package com.dentist.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_clinics")
@IdClass(UserClinicId.class)
public class UserClinic {
    
    @Column(name = "is_default")
    private Boolean isDefault;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "clinic_id", insertable = false, updatable = false)
    private Clinic clinic;
    
	public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    @JsonIgnore
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @JsonIgnore
    public Clinic getClinic() {
        return clinic;
    }
    
    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }
}
