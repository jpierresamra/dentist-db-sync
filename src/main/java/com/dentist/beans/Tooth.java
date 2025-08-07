package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "teeth")
public class Tooth implements Serializable, Persistable<Integer> {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -5224257128912944927L;

    public static final String TOOTH_GROUP_TOOTH = "tooth";
    public static final String TOOTH_GROUPARCH = "arch";
    public static final String TOOTH_GROUP_QUADRANT = "quadrant";
    public static final String TOOTH_GROUP_MOUTH = "full_mouth";

    
	@Id
	@Column(name = "id")
	private Integer id;

    @Column(name = "name")
	private String	name;

	@Column(name = "update_date")
	private Date updateDate;
	
	@Column(name = "tooth_group")
	private String toothGroup;
	
	@Column(name = "tooth_numbers")
	private String toothNumbers;
	
	@Column(name = "tooth_order")
	private int toothOrder;

	@Transient
	private boolean isNew = false;
	
    public Tooth() {
        super();
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public int getToothOrder() {
		return toothOrder;
	}

	public void setToothOrder(int toothOrder) {
		this.toothOrder = toothOrder;
	}

	public String getToothGroup() {
		return toothGroup;
	}

	public void setToothGroup(String toothGroup) {
		this.toothGroup = toothGroup;
	}


	public String getToothNumbers() {
		return toothNumbers;
	}

	public void setToothNumbers(String toothNumbers) {
		this.toothNumbers = toothNumbers;
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
		return "Tooth [id=" + id + ", name=" + name + ", updateDate=" + updateDate + ", toothGroup=" + toothGroup
				+ ", toothNumbers=" + String.join(", ", toothNumbers) + ", toothOrder=" + toothOrder + "]";
	}
    
}
