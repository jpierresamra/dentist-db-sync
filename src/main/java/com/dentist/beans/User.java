package com.dentist.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "users")
public class User implements Serializable, Persistable<UUID>, ComparableSyncItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_DELETED = 2;
	
	public User() {
	}

	@Id
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private UUID id;

	@Column(name = "user_name", unique = true, nullable = false)
	private String username;
	
	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;
	
	@Column(name = "phone")
	private String phone;

	@Column(name = "status")
	private int status;

	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;
	
	@Column(name = "update_date")
	private Date updateDate;
	
	@ManyToOne
	@JoinColumn(name = "account_id", referencedColumnName = "account_id", nullable = false)
	private Account account;

	@ManyToMany
	@JoinTable(name = "user_clinics", // Name of the join table
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), // Foreign key for User
			inverseJoinColumns = @JoinColumn(name = "clinic_id", referencedColumnName = "clinic_id") 
	)
	private List<Clinic> clinics;

	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "role")
	private Set<Role> roles;

	@Transient
	private String token;

	@Transient
	private boolean isNew = false;
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<Clinic> getClinics() {
		return clinics;
	}

	public void setClinics(List<Clinic> clinics) {
		this.clinics = clinics;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

}
