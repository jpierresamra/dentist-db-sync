package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "treatments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Treatment implements Serializable, Persistable<UUID>, ComparableSyncItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -689438842104412947L;
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
	
	@Column(name = "operation_id", nullable = true)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID operationId;

	@Column(name = "operate_date")
	private Date operateDate;

	@OneToOne
	@JoinColumn(name = "procedure_id", referencedColumnName = "id", nullable = false)
	private Procedure procedure;

	@OneToMany(mappedBy = "treatment", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference  // This manages the forward reference
	private Set<TreatmentTooth> treatmentTeeth;
	
	@Column(name = "description")
	private String description;

	@Column(name = "fee")
	private BigDecimal fee;

	@Column(name = "status")
	private int status;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Transient
	private boolean isNew = false;
	
	public Treatment() {
		super();
		fee = new BigDecimal(0.00);
		operateDate = new Date();
		treatmentTeeth = new HashSet<>();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getOperateDateString() {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
		String operateDateString = sf.format(this.operateDate);
		return operateDateString;
	}
	
	public Date getOperateDate() {
		return operateDate;
	}

	public void setOperateDate(Date operateDate) {
		this.operateDate = operateDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public String getFeeString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.fee);
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public UUID getOperationId() {
		return operationId;
	}

	public void setOperationId(UUID operationId) {
		this.operationId = operationId;
	}

	public Set<TreatmentTooth> getTreatmentTeeth() {
		return treatmentTeeth;
	}

	public void setTreatmentTeeth(Set<TreatmentTooth> treatmentTeeth) {
		this.treatmentTeeth = treatmentTeeth;
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
		return "Treatment [id=" + id + ", customer=" + customer + ", accountId=" + accountId + ", operationId="
				+ operationId + ", operateDate=" + operateDate + ", procedure=" + procedure + ", treatmentTeeth="
				+ treatmentTeeth + ", description=" + description + ", fee=" + fee + ", status=" + status
				+ ", createDate=" + createDate + ", updateDate=" + updateDate + "]";
	}
}
