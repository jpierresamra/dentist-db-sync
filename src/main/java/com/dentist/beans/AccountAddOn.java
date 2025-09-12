package com.dentist.beans;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "account_add_ons")
public class AccountAddOn implements Serializable, Persistable<UUID>, ComparableSyncItem {
    
    private static final long serialVersionUID = 1L;

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private int accountId;

    @ManyToOne
    @JoinColumn(name = "add_on_id", referencedColumnName = "id")
    private AddOn addOn;

    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate; // null for one-time purchases

    @Column(name = "price_paid")
    private BigDecimal pricePaid; // Actual price paid (may differ from current price)

    @Column(name = "currency")
    private String currency;

    @Column(name = "status")
    private int status; // 1=active, 0=inactive, 2=expired, etc.

    @Column(name = "auto_renew")
    private Boolean autoRenew; // For recurring add-ons
    
	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;

	@Column(name = "update_date")
	private Date updateDate;
	
	@Transient
	private boolean isNew = false;

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
    
    public AddOn getAddOn() { 
        return addOn; 
    }
    
    public void setAddOn(AddOn addOn) { 
        this.addOn = addOn; 
    }
    
    public Date getPurchaseDate() { 
        return purchaseDate; 
    }
    
    public void setPurchaseDate(Date purchaseDate) { 
        this.purchaseDate = purchaseDate; 
    }
    
    public Date getStartDate() { 
        return startDate; 
    }
    
    public void setStartDate(Date startDate) { 
        this.startDate = startDate; 
    }
    
    public Date getEndDate() { 
        return endDate; 
    }
    
    public void setEndDate(Date endDate) { 
        this.endDate = endDate; 
    }
    
    public BigDecimal getPricePaid() { 
        return pricePaid; 
    }
    
    public void setPricePaid(BigDecimal pricePaid) { 
        this.pricePaid = pricePaid; 
    }
    
    public String getCurrency() { 
        return currency; 
    }
    
    public void setCurrency(String currency) { 
        this.currency = currency; 
    }
    
    public int getStatus() { 
        return status; 
    }
    
    public void setStatus(int status) { 
        this.status = status; 
    }
    
    public Boolean getAutoRenew() { 
        return autoRenew; 
    }
    
    public void setAutoRenew(Boolean autoRenew) { 
        this.autoRenew = autoRenew; 
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
    
    
}
