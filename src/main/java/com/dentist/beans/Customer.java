package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import com.dentist.util.StringUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "customers")
public class Customer implements Serializable, Persistable<UUID>, ComparableSyncItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String[] bloodTypes = { "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-" };
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_DELETED = 2;
	
	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;

	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "email")
	private String email;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "reference_number")
	private String referenceNumber;

	@Column(name = "address")
	private String address;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@Column(name = "home_number")
	private String homeNumber;

	@Column(name = "gender")
	private String gender;

	@Column(name = "nationality")
	private String nationality;

	@Column(name = "profession")
	private String profession;

	@Column(name = "blood_type")
	private String bloodType;

	@Column(name = "notes")
	private String notes;

	@Column(name = "treatment_plan")
	private String treatmentPlan;

	@Column(name = "medical_history")
	private String medicalHistory;
	
	@Column(name = "status")
	private int status;
	
	@Column(name = "account_id")
	private int accountId;
	
	@ManyToOne
	@JoinColumn(name = "class_type_id", referencedColumnName = "id", nullable = true)
	private ClassType classType;
	
	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;
	
	@Transient
	private Date nextAppointmentDate;
	
	@Column(name = "referral")
	private String referral;

	@Transient
	private boolean isNew = false;
	
	public String getGender() {
		return gender;
	}

	public void setSexe(String gender) {
		this.gender = gender;
	}

	public String getNationality() {
		return StringUtil.getEmptyStringForNull(nationality);
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getProfession() {
		return StringUtil.getEmptyStringForNull(profession);
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getBloodType() {
		return StringUtil.getEmptyStringForNull(bloodType);
	}

	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return StringUtil.getEmptyStringForNull(lastName);
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return StringUtil.getEmptyStringForNull(address);
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMobileNumber() {
		return StringUtil.getEmptyStringForNull(mobileNumber);
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getHomeNumber() {
		return StringUtil.getEmptyStringForNull(homeNumber);
	}

	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
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

	public String getReferenceNumber() {
		return StringUtil.getEmptyStringForNull(referenceNumber);
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getText() {
		return this.firstName + " " + this.lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getTreatmentPlan() {
		return treatmentPlan;
	}

	public void setTreatmentPlan(String treatmentPlan) {
		this.treatmentPlan = treatmentPlan;
	}

	public String getMedicalHistory() {
		return medicalHistory;
	}

	public void setMedicalHistory(String medicalHistory) {
		this.medicalHistory = medicalHistory;
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

	public Date getNextAppointmentDate() {
		return nextAppointmentDate;
	}

	public void setNextAppointmentDate(Date nextAppointmentDate) {
		this.nextAppointmentDate = nextAppointmentDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getReferral() {
		return referral;
	}

	public void setReferral(String referral) {
		this.referral = referral;
	}
	
	public ClassType getClassType() {
		return classType;
	}

	public void setClassType(ClassType classType) {
		this.classType = classType;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	@Override
	public boolean isNew() {
		return isNew;
	}
	
	@Override
	public String toString() {
		// return a customer with all the properties
		return "Customer [id=" + id + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName="
				+ lastName + ", referenceNumber=" + referenceNumber + ", address=" + address + ", mobileNumber="
				+ mobileNumber + ", homeNumber=" + homeNumber + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + ", accountId=" + accountId + ", bloodType=" + bloodType 
				+ ", notes=" + notes + ", treatmentPlan=" + treatmentPlan + ", medicalHistory=" + medicalHistory + ", status=" + status 
				+ ", email=" + email + ", referral=" + referral+ ", classType=" + classType+"]";
	}		

}
//