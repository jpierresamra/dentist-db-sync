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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "appointments")
public class Appointment implements Serializable, Persistable<UUID>, ComparableSyncItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_DELETED = 2;

	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
	private Customer customer;

	@Column(name = "account_id", nullable = false)
	private int accountId;

	@Column(name = "start", nullable = false)
	private Date start;

	@Column(name = "duration_minutes", nullable = false)
	private Integer durationMinutes;

	@Column(name = "title", length = 128)
	private String title;

	@Column(name = "status", nullable = false)
	private Integer status;

	@Column(name = "clinic_id", nullable = false)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID clinicId;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Transient
	private boolean isNew = false;

	// Getters and Setters
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Integer getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public UUID getClinicId() {
		return clinicId;
	}

	public void setClinicId(UUID clinicId) {
		this.clinicId = clinicId;
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

	@Override
	public String toString() {
		return "Appointment{" + "id=" + id + ", customerId=" + (customer != null ? customer.getId() : null)
				+ ", clinicId=" + clinicId + ", start=" + start + ", durationMinutes=" + durationMinutes + ", title='"
				+ title + '\'' + ", status=" + status + ", createDate=" + createDate + ", updateDate=" + updateDate
				+ " accountId=" + accountId + '}';
	}
}