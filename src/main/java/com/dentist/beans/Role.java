package com.dentist.beans;

public enum Role {
	ADMIN, 
	DOCTOR,
	ASSISTANT,
	SECRETARY;
	
	public String getAuthority() {
		return "ROLE_" + this.name();
	}
}
