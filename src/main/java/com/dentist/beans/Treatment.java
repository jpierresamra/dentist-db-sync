package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "treatments")
public class Treatment implements Serializable, Persistable<UUID>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -689438842104412947L;
	
	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;
	
	@Column(name = "customer_id")
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID		customerId;
	
	@Column(name = "account_id", nullable = false)
	private int accountId;
	
	@Column(name = "treatment_date")
	private Date		treatmentDate;
	
	@Column(name = "tooth")
	private String		tooth;
	
	@Column(name = "description")
	private String		description;
	
	@Column(name = "fee")
	private BigDecimal	fee;
	
	@Column(name = "paid")
	private BigDecimal	paid;
	
	@Column(name = "remaining")
	private BigDecimal	remaining;
	
	@Column(name = "status")
	private int status;
	
	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Transient
	private boolean isNew = false;
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setRemaining(BigDecimal remaining) {
		this.remaining = remaining;
	}
	
	public Date getTreatmentDate()
	{
		return treatmentDate;
	}

	public String getTreatmentDateString()
	{
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
		String treatmentDateString = sf.format(this.treatmentDate);
		return treatmentDateString;
	}

	public void setTreatmentDate(Date treatmentDate)
	{
		this.treatmentDate = treatmentDate;
	}

	public String getTooth()
	{
		return tooth;
	}

	public void setTooth(String tooth)
	{
		this.tooth = tooth;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public BigDecimal getFee()
	{
		return fee;
	}

	public String getFeeString()
	{
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.fee);
	}

	public void setFee(BigDecimal fee)
	{
		this.fee = fee;
	}

	public BigDecimal getPaid()
	{
		return paid;
	}

	public String getPaidString()
	{
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(paid);
	}

	public void setPaid(BigDecimal paid)
	{
		this.paid = paid;
	}


	public UUID getCustomerId() {
		return customerId;
	}

	public void setCustomerId(UUID customerId) {
		this.customerId = customerId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BigDecimal getRemaining() {
		remaining = fee.subtract(paid);
		return remaining;
	}
	
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
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
		return "Treatment [id=" + id + ", customerId=" + customerId + ", treatmentDate=" + treatmentDate + ", tooth="
				+ tooth + ", description=" + description + ", fee=" + fee + ", paid=" + paid + ", remaining="
				+ remaining + ", status=" + status + ", createDate=" + createDate + ", updateDate=" + updateDate + ", accountId=" + accountId + "]";
	}

}
