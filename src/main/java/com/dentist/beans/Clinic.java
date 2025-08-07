package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "clinics")
public class Clinic implements Serializable, Persistable<UUID>, ComparableSyncItem {

	
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_DELETED = 2;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Clinic() {
	}
	
	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "clinic_id")
	private UUID clinicId;
	
	@Column(name = "clinic_name")
	private String clinicName;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "status")
	private int status;
	
	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;
	
	@Column(name = "update_date")
	private Date updateDate;
	
	@Column(name = "account_id")
	private int accountId;

	@Transient
	private boolean isNew = false;
	
	public UUID getClinicId() {
		return clinicId;
	}

	public void setClinicId(UUID clinicId) {
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
	
	@Override
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
		

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	@Override
	public boolean isNew() {
		return isNew;
	}

	public String toString() {
		return "Clinic [clinicId=" + clinicId + ", clinicName=" + clinicName + ", address=" + address + ", phone="
				+ phone + ", status=" + status + ", createDate=" + createDate + ", updateDate=" + updateDate
				+ ", accountId=" + accountId + "]";
	}

	@Override
	public UUID getId() {
		return this.getClinicId();
	}

    
}
