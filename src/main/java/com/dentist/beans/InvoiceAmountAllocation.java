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
import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "invoice_amount_allocations")
public class InvoiceAmountAllocation implements Serializable, Persistable<UUID>, ComparableSyncItem {

	private static final long serialVersionUID = -987654321098765432L;
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_DELETED = 2;

	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;

	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "invoice_id", referencedColumnName = "id", nullable = true)
	@JsonIgnore
	private Invoice invoice;

	// Custom getter for JSON serialization - only returns invoice ID
	@JsonProperty("invoiceId")
	public UUID getInvoiceId() {
		return invoice != null ? invoice.getId() : null;
	}
	
	// Custom getter for JSON serialization - only returns invoice number
	@JsonProperty("invoiceNumber")
	public String getInvoiceNumber() {
		return invoice != null ? invoice.getInvNumber() : null;
	}
    
	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Column(name = "currency", nullable = false, length = 3)
	private String currency;

	@Column(name = "account_id", nullable = false)
	private int accountId;

	@Column(name = "status", nullable = false)
	private int status;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Transient
	private boolean isNew = false;

	public InvoiceAmountAllocation() {
		super();
		this.amount = BigDecimal.ZERO;
		this.currency = "USD";
		this.status = STATUS_ACTIVE;
	}

	// Getters and Setters
	@Override
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

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getAmountString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.amount);
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
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

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	@Override
	public boolean isNew() {
		return isNew;
	}

	@Override
	public String toString() {
		return "InvoiceAmountAllocation [id=" + id + ", customerId=" + (customer != null ? customer.getId() : null)
				+ ", invoiceId=" + (invoice != null ? invoice.getId() : null) + ", amount=" + amount + ", currency="
				+ currency + ", accountId=" + accountId + ", status=" + status + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + "]";
	}
}
