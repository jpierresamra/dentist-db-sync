package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "invoices")
public class Invoice implements Serializable, Persistable<UUID>, ComparableSyncItem {

	/**
	 * 
	 */	
	private static final long serialVersionUID = -123456789012345678L;
	public static final int STATUS_OPENED = 1;
	public static final int STATUS_DELETED = 2;
	public static final int STATUS_CLOSED = 3;
	
	public static final int PAYMENT_STATUS_UNPAID = 1;
	public static final int PAYMENT_STATUS_PARTIALLY_PAID = 2;
	public static final int PAYMENT_STATUS_PAID = 3;

	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;
	
	@Column(name = "inv_number", nullable = false, length = 32)
	private String invNumber;

	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
	private Customer customer;

	@Column(name = "account_id", nullable = false)
	private int accountId;

	@Column(name = "issue_date")
	private Date issueDate;

	@Column(name = "original_amount", nullable = false)
	private BigDecimal originalAmount;

	@Column(name = "inv_discount_type", nullable = false, length = 16)
	private String invDiscountType;

	@Column(name = "inv_discount_value", nullable = false)
	private BigDecimal invDiscountValue;

	@Column(name = "inv_discount_amount", nullable = false)
	private BigDecimal invDiscountAmount;

	@Column(name = "total_item_discount_amount", nullable = false)
	private BigDecimal totalItemDiscountAmount;

	@Column(name = "total_discount_amount", nullable = false)
	private BigDecimal totalDiscountAmount;

	@Column(name = "final_amount", nullable = false)
	private BigDecimal finalAmount;
	
	@Transient
	private BigDecimal paymentAmount;

	@Transient
	private boolean useUnallocatedCredit;
	
	@Column(name = "status", nullable = false)
	private int status;
	
	@Column(name = "payment_status", nullable = false)
	private int paymentStatus;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;	
	
	@Column(name = "update_date")
	private Date updateDate;	
	
	@OneToMany(mappedBy = "invoice", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InvoiceItem> invoiceItems;
	
	@OneToMany(mappedBy = "invoice", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InvoiceAmountAllocation> invoiceAmountAllocations;
	

	// Helper methods for payment calculations
	public BigDecimal getTotalPaidAmount() {
		BigDecimal totalPaid = BigDecimal.ZERO;
		if (invoiceAmountAllocations != null) {
			for (InvoiceAmountAllocation allocation : invoiceAmountAllocations) {
				if (allocation.getStatus() == InvoiceAmountAllocation.STATUS_ACTIVE) {
					totalPaid = totalPaid.add(allocation.getAmount());
				}
			}
		}
		return totalPaid;
	}

	public String getTotalPaidAmountString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(getTotalPaidAmount());
	}

	public BigDecimal getRemainingAmount() {
		// Final amount minus total paid amount
		BigDecimal totalPaid = getTotalPaidAmount();
		return this.finalAmount.subtract(totalPaid);
	}

	public String getRemainingAmountString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(getRemainingAmount());
	}

	public boolean isFullyPaid() {
		return getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0;
	}

	public boolean isPartiallyPaid() {
		BigDecimal totalPaid = getTotalPaidAmount();
		return totalPaid.compareTo(BigDecimal.ZERO) > 0 && totalPaid.compareTo(this.finalAmount) < 0;
	}

	public void updatePaymentStatus() {
		if (isFullyPaid()) {
			this.paymentStatus = PAYMENT_STATUS_PAID;
		} else if (isPartiallyPaid()) {
			this.status = PAYMENT_STATUS_PARTIALLY_PAID;
		} else if (this.status != STATUS_DELETED) {
			this.status = PAYMENT_STATUS_UNPAID;
		}
	}
	
	@Transient
	private boolean isNew = false;

	public Invoice() {
		super();
		originalAmount = new BigDecimal(0.000);
		invDiscountType = "amount";
		invDiscountValue = new BigDecimal(0.000);
		invDiscountAmount = new BigDecimal(0.000);
		totalItemDiscountAmount = new BigDecimal(0.000);
		totalDiscountAmount = new BigDecimal(0.000);
		finalAmount = new BigDecimal(0.000);
		issueDate = new Date();
		useUnallocatedCredit = false;
		status = STATUS_OPENED;
		paymentStatus = PAYMENT_STATUS_UNPAID;
	}

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

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public String getIssueDateString() {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
		String issueDateString = sf.format(this.issueDate);
		return issueDateString;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}

	public String getOriginalAmountString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.originalAmount);
	}

	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

	public String getInvDiscountType() {
		return invDiscountType;
	}

	public void setInvDiscountType(String invDiscountType) {
		this.invDiscountType = invDiscountType;
	}

	public BigDecimal getInvDiscountValue() {
		return invDiscountValue;
	}

	public String getInvDiscountValueString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.invDiscountValue);
	}

	public void setInvDiscountValue(BigDecimal invDiscountValue) {
		this.invDiscountValue = invDiscountValue;
	}

	public BigDecimal getInvDiscountAmount() {
		return invDiscountAmount;
	}

	public String getInvDiscountAmountString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.invDiscountAmount);
	}

	public void setInvDiscountAmount(BigDecimal invDiscountAmount) {
		this.invDiscountAmount = invDiscountAmount;
	}

	public BigDecimal getTotalItemDiscountAmount() {
		return totalItemDiscountAmount;
	}

	public String getTotalItemDiscountAmountString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.totalItemDiscountAmount);
	}

	public void setTotalItemDiscountAmount(BigDecimal totalItemDiscountAmount) {
		this.totalItemDiscountAmount = totalItemDiscountAmount;
	}

	public BigDecimal getTotalDiscountAmount() {
		return totalDiscountAmount;
	}

	public String getTotalDiscountAmountString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.totalDiscountAmount);
	}

	public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
	}

	public BigDecimal getFinalAmount() {
		return finalAmount;
	}

	public String getFinalAmountString() {
		DecimalFormat df = new DecimalFormat("###,##0.000");
		return df.format(this.finalAmount);
	}

	public void setFinalAmount(BigDecimal finalAmount) {
		this.finalAmount = finalAmount;
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
	
	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}
	
	public List<InvoiceAmountAllocation> getInvoiceAmountAllocations() {
		return invoiceAmountAllocations;
	}

	public void setInvoiceAmountAllocations(List<InvoiceAmountAllocation> invoiceAmountAllocations) {
		this.invoiceAmountAllocations = invoiceAmountAllocations;
	}
	
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getInvNumber() {
		return invNumber;
	}

	public void setInvNumber(String invNumber) {
		this.invNumber = invNumber;
	}


	public boolean getUseUnallocatedCredit() {
		return useUnallocatedCredit;
	}

	public void setUseUnallocatedCredit(boolean useUnallocatedCredit) {
		this.useUnallocatedCredit = useUnallocatedCredit;
	}

	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	@Override
	public boolean isNew() {
		return isNew;
	}

	@Override
	public String toString() {
		return "Invoice [id=" + id + ", invNumber=" + invNumber + ", customerId=" + customer.getId()
				+ ", accountId=" + accountId + ", issueDate=" + issueDate + ", originalAmount=" + originalAmount
				+ ", invDiscountType=" + invDiscountType + ", invDiscountValue=" + invDiscountValue
				+ ", invDiscountAmount=" + invDiscountAmount + ", totalItemDiscountAmount=" + totalItemDiscountAmount
				+ ", totalDiscountAmount=" + totalDiscountAmount + ", finalAmount=" + finalAmount + ", status="
				+ status + ", paymentStatus=" + paymentStatus + ", createDate=" + createDate + ", updateDate=" 
				+ updateDate + "]";
	}
}
