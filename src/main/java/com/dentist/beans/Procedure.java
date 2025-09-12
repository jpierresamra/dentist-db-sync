package com.dentist.beans;

import java.io.Serializable;
import java.math.BigDecimal;
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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "procedures")
public class Procedure implements Serializable, Persistable<UUID>, ComparableSyncItem {

    public static final int STATUS_CREATED = 1;
	public static final int STATUS_DELETED = 2;
	public static final int STATUS_DISABLED = 3;
	
    public static final String PRICE_PER_TOOTH = "per_tooth";
    public static final String PRICE_PER_SURFACE = "per_surface";
    public static final String PRICE_PER_QUADRANT = "per_quadrant";
    public static final String PRICE_PER_ARCH = "per_arch";
    public static final String PRICE_FLAT_FEE = "flat_fee";
	
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(name = "code", nullable = false)
    private String code;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
    
    @Column(name = "pricing_type")
    private String pricingType;

    @Column(name = "account_id", nullable = false)
    private int accountId;

    @Column(name = "create_date", updatable = false, insertable = true)
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;
    
    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "display")
    private String display;

    @Transient
    private String[] surfaces;
    
    @Transient
    private boolean isNew = false;
    
    public Procedure() {
        super();
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPricingType() {
		return pricingType;
	}

	public void setPricingType(String pricingType) {
		this.pricingType = pricingType;
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
	
    public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
	
	public String[] getSurfaces() {
		return surfaces;
	}

	public void setSurfaces(String[] surfaces) {
		this.surfaces = surfaces;
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
		return "Procedure [id=" + id + ", code=" + code + ", category=" + category + ", name=" + name + ", price="
				+ price + ", pricingType=" + pricingType + ", accountId=" + accountId + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + ", status=" + status + "]";
	}

}
