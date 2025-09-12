package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserClinicId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID user;
    private UUID clinic;
    
    // Default constructor
    public UserClinicId() {
    	super();
    }
    
    // Constructor
    public UserClinicId(UUID user, UUID clinic) {
        this.user = user;
        this.clinic = clinic;
    }
    
	public UUID getUser() {
		return user;
	}

	public void setUser(UUID user) {
		this.user = user;
	}

	public UUID getClinic() {
		return clinic;
	}

	public void setClinic(UUID clinic) {
		this.clinic = clinic;
	}

	// equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserClinicId that = (UserClinicId) o;
        return Objects.equals(user, that.user) && Objects.equals(clinic, that.clinic);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user, clinic);
    }
}
