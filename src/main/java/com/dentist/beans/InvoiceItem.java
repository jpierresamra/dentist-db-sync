package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "invoice_items")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InvoiceItem implements Serializable, Persistable<UUID>, ComparableSyncItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -987654321098765432L;
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_DELETED = 2;

	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;
	
	@ManyToOne
	@JoinColumn(name = "invoice_id", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	private Invoice invoice;
		
	@ManyToOne
	@JoinColumn(name = "operation_id", referencedColumnName = "id", nullable = false)
	private Operation operation;

	@Column(name = "account_id", nullable = false)
	private int accountId;

	@Column(name = "code", nullable = false, length = 64)
	private String code;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;

	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@Column(name = "discount_type")
	private int discountType;

	@Column(name = "discount_value", nullable = false)
	private BigDecimal discountValue;

	@Column(name = "discount_amount", nullable = false)
	private BigDecimal discountAmount;

	@Column(name = "final_price", nullable = false)
	private BigDecimal finalPrice;

	@Column(name = "status", nullable = false)
	private int status;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;
	
	@Transient
	private UUID operationId;

	@Transient
	private boolean isNew = false;

	public InvoiceItem() {
		super();
		price = new BigDecimal(0.000);
		discountType = 1;
		discountValue = new BigDecimal(0.000);
		discountAmount = new BigDecimal(0.000);
		finalPrice = new BigDecimal(0.000);
		status = STATUS_CREATED;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	// Custom getter for JSON serialization - only returns operation ID
	@JsonProperty("operationId")
	public UUID getOperationId() {
		return operation != null ? operation.getId() : this.operationId;
	}
	
	public void setOperationId(UUID operationId) {
		this.operationId = operationId;
	}
	
	// Custom getter for JSON serialization - only returns invoice ID
	@JsonProperty("invoiceId")
	public UUID getInvoiceId() {
		return invoice != null ? invoice.getId() : null;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getPriceString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.price);
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getDiscountType() {
		return discountType;
	}

	public void setDiscountType(int discountType) {
		this.discountType = discountType;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public String getDiscountValueString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.discountValue);
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public BigDecimal getDiscountAmount() {
		return discountAmount;
	}

	public String getDiscountAmountString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.discountAmount);
	}

	public void setDiscountAmount(BigDecimal discountAmount) {
		this.discountAmount = discountAmount;
	}

	public BigDecimal getFinalPrice() {
		return finalPrice;
	}

	public String getFinalPriceString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.finalPrice);
	}

	public void setFinalPrice(BigDecimal finalPrice) {
		this.finalPrice = finalPrice;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "InvoiceItem [id=" + id + ", invoiceId=" + getInvoiceId()
				+ ", operationId=" + getOperationId() + ", accountId=" + accountId
				+ ", code=" + code + ", name=" + name +" price=" + price + ", discountType=" + discountType + ", discountValue="
				+ discountValue + ", discountAmount=" + discountAmount + ", finalPrice=" + finalPrice + ", status="
				+ status + ", createDate=" + createDate + ", updateDate=" + updateDate + "]";
	}
}
