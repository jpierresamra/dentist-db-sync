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

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "operations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Operation implements Serializable, Persistable<UUID>, ComparableSyncItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -689438842104412947L;
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_DELETED = 2;
	public static final int STATUS_INPROGRESS = 3;
	public static final int STATUS_COMPLETED = 4;
	public static final int STATUS_CANCELED = 5;

	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "doctor_id", referencedColumnName = "id", nullable = false)
	private User doctor;
	
	@ManyToOne
	@JoinColumn(name = "clinic_id", referencedColumnName = "clinic_id", nullable = false)
	private Clinic clinic;

	@Column(name = "account_id", nullable = false)
	private int accountId;

	@Column(name = "operate_date")
	private Date operateDate;

	@OneToOne
	@JoinColumn(name = "procedure_id", referencedColumnName = "id", nullable = false)
	private Procedure procedure;
	
	@OneToOne
	@JoinColumn(name = "treatment_id", referencedColumnName = "id", nullable = true)
	private Treatment treatment;
	
	@OneToMany(mappedBy = "operation", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference  // This manages the forward reference
	private Set<OperationTooth> operationTeeth;

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

	@OneToOne
	@JoinColumn(name = "id", referencedColumnName = "operation_id")
	@JsonIgnore  
	private InvoiceItem invoiceItem;

	@Transient
    private Boolean billed;

	@Transient
	private boolean isNew = false;
		
	public Operation() {
		super();
		fee = new BigDecimal(0.00);
		operateDate = new Date();
		operationTeeth = new HashSet<>();
	}

	@Override
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
	public Set<OperationTooth> getOperationTeeth() {
		return operationTeeth;
	}

	public void setOperationTeeth(Set<OperationTooth> operationTeeth) {
		this.operationTeeth = operationTeeth;
	}
	// Convenience methods for working with teeth
	public Set<Tooth> getTeeth() {
		Set<Tooth> teeth = new HashSet<>();
		if (operationTeeth != null) {
			for (OperationTooth ot : operationTeeth) {
				teeth.add(ot.getTooth());
			}
		}
		return teeth;
	}

	public void addTooth(Tooth tooth) {
		addTooth(tooth, "ALL");
	}

	public void addTooth(Tooth tooth, String surface) {
		if (operationTeeth == null) {
			operationTeeth = new HashSet<>();
		}
		String finalSurface = surface != null ? surface : "ALL";
		OperationTooth operationTooth = new OperationTooth(this, tooth, finalSurface);
		operationTeeth.add(operationTooth);
	}

	public void addToothWithSurfaces(Tooth tooth, List<String> surfaces) {
		if (surfaces == null || surfaces.isEmpty()) {
			addTooth(tooth, "ALL");
			return;
		}
		
		for (String surface : surfaces) {
			addTooth(tooth, surface);
		}
	}

	public void removeTooth(Tooth tooth) {
		if (operationTeeth != null) {
			operationTeeth.removeIf(ot -> ot.getTooth().equals(tooth));
		}
	}

	public void removeToothSurface(Tooth tooth, String surface) {
		if (operationTeeth != null) {
			String finalSurface = surface != null ? surface : "ALL";
			operationTeeth.removeIf(ot -> ot.getTooth().equals(tooth) && finalSurface.equals(ot.getSurface()));
		}
	}

	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}

	public Boolean getBilled() {
		return this.billed;
	}
	
	public void setBilled(Boolean billed) {
		this.billed = billed;
	}
	
	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
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
		return "Operation [id=" + id + ", customer=" + customer + ", accountId=" + accountId + ", operateDate="
				+ operateDate + ", procedure=" + procedure + ", treatment=" + treatment 
				+ ", operationTeethCount=" + (operationTeeth != null ? operationTeeth.size() : 0)
				+ ", description=" + description + ", fee=" + fee + ", status=" + status + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + "]";
	}
}
