package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "recalls")
public class Recall implements Serializable, Persistable<UUID>, ComparableSyncItem {

	private static final long serialVersionUID = 1L;
	
	// Status constants
	public static final int STATUS_SCHEDULED = 1;
	public static final int STATUS_CALLED = 3;
	public static final int STATUS_CANCELLED = 2;
	
	// Priority constants
	public static final int PRIORITY_LOW = 1;
	public static final int PRIORITY_MEDIUM = 2;
	public static final int PRIORITY_HIGH = 3;

	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
	private Customer customer;

	@Column(name = "account_id")
	private int accountId;

	@Column(name = "recall_date")
	private Date recallDate;

	@Column(name = "status")
	private int status = STATUS_SCHEDULED;

	@Column(name = "recall_type")
	private String recallType;

	@Column(name = "notes")
	private String notes;

	@Column(name = "priority")
	private int priority = PRIORITY_LOW;

	@ManyToOne
	@JoinColumn(name = "created_by", referencedColumnName = "id", nullable = true)
	private User createdBy;

	@Column(name = "called_date")
	private Date calledDate;

	@Column(name = "scheduled_appointment_id")
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID scheduledAppointmentId;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;
	
	@ManyToOne
	@JoinColumn(name = "called_by", referencedColumnName = "id", nullable = true)
	private User calledBy;

	@Transient
	private boolean isNew = false;
	
	// Default constructor
	public Recall() {
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public Date getRecallDate() {
		return recallDate;
	}

	public void setRecallDate(Date recallDate) {
		this.recallDate = recallDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRecallType() {
		return recallType;
	}

	public void setRecallType(String recallType) {
		this.recallType = recallType;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Date getCalledDate() {
		return calledDate;
	}

	public void setCalledDate(Date calledDate) {
		this.calledDate = calledDate;
	}

	public UUID getScheduledAppointmentId() {
		return scheduledAppointmentId;
	}

	public void setScheduledAppointmentId(UUID scheduledAppointmentId) {
		this.scheduledAppointmentId = scheduledAppointmentId;
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public void setCalledBy(User calledBy) {
		this.calledBy = calledBy;
	}
	
	@JsonIgnore
	public User getCalledBy() {
		return calledBy;
	}
	
	public String getCalledByName() {
		return (calledBy != null) ? calledBy.getFirstName() + " " + calledBy.getLastName() : null;
	}
	
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	
	@JsonIgnore
	public User getCreatedBy() {
		return createdBy;
	}
	
	public String getCreatedByName() {
		return (createdBy != null) ? createdBy.getFirstName() + " " + createdBy.getLastName() : null;
	}
	
	// Utility methods
	public String getStatusText() {
		switch (status) {
			case STATUS_SCHEDULED: return "Scheduled";
			case STATUS_CALLED: return "Called";
			case STATUS_CANCELLED: return "Cancelled";
			default: return "Unknown";
		}
	}

	public String getPriorityText() {
		switch (priority) {
			case PRIORITY_LOW: return "Low";
			case PRIORITY_MEDIUM: return "Medium";
			case PRIORITY_HIGH: return "High";
			default: return "Unknown";
		}
	}

	public boolean isActive() {
		return status == STATUS_SCHEDULED;
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
		return "Recall [id=" + id + ", customerId=" + customer.getId() + ", accountId=" + accountId 
				+ ", recallDate=" + recallDate + ", status=" + status + ", recallType=" + recallType 
				+ ", notes=" + notes + ", priority=" + priority + ", createdBy=" + createdBy 
				+ ", calledDate=" + calledDate + ", calledBy="+ calledBy +", scheduledAppointmentId=" + scheduledAppointmentId 
				+ ", createDate=" + createDate + ", updateDate=" + updateDate + "]";
	}

}
