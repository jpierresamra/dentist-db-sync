package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "clinics")
public class Clinic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Clinic() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "clinic_id")
	private long clinicId;
	
	@Column(name = "clinic_name")
	private String clinicName;
	
	@Column(name = "create_date", updatable = false, insertable = false)
	private Date createDate;
	
	public long getClinicId() {
		return clinicId;
	}

	public void setClinicId(long clinicId) {
		this.clinicId = clinicId;
	}

	public String getClinicName() {
		return clinicName;
	}

	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String toString() {
		return "Clinic [clinicId=" + clinicId + ", clinicName=" + clinicName + ", createDate=" + createDate + "]";
	}

    
}
