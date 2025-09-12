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
@Table(name = "payments")
public class Payment implements Serializable , Persistable<UUID>, ComparableSyncItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -123456789012345679L;

	// Payment method constants
	public static final String METHOD_CASH = "cash";
	public static final String METHOD_CARD = "card";
	public static final String METHOD_BANK_TRANSFER = "bank_transfer";
	public static final String METHOD_CHECK = "check";

	// Currency constants
	public static final String CURRENCY_USD = "USD";
	public static final String CURRENCY_EUR = "EUR";
	
	public static final int STATUS_CREATED = 1;
	public static final int STATUS_DELETED = 2;

	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;

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

	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
	private Customer customer;
	
	@Column(name = "reference_number")
	private String referenceNumber;
	
	@Column(name = "amount", nullable = true, precision = 15, scale = 3)
	private BigDecimal amount;

	@Column(name = "currency", nullable = false, length = 3)
	private String currency;

	@Column(name = "method", nullable = false, length = 32)
	private String method;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;

	@Column(name = "account_id", nullable = false)
	private int accountId;
	
	@Column(name = "status", nullable = false)
	private int status;
	
	@Transient
	private boolean isNew = false;
	
	public Payment() {
		super();
		amount = new BigDecimal(0.000);
		currency = CURRENCY_USD;
		method = METHOD_CASH;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public String getAmountString() {
		if (amount == null) {
			return "0.000";
		}
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

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getCreateDateString() {
		if (createDate == null) {
			return "";
		}
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sf.format(this.createDate);
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}

	public String getUpdateDateString() {
		if (updateDate == null) {
			return "";
		}
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sf.format(this.updateDate);
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

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
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
		return "Payment [id=" + id + ", invoice=" + (invoice != null ? invoice.getId() : "null") + ", customer="
				+ (customer != null ? customer.getId() : "null") + ", referenceNumber=" + referenceNumber + ", amount="
				+ amount + ", currency=" + currency + ", method=" + method + ", createDate=" + getCreateDateString()
				+ ", updateDate=" + getUpdateDateString() + ", accountId=" + accountId + ", status=" + status + "]";
	}
}
