package com.dentist.beans;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Persistable;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "accounts")
public class Account implements Serializable, Persistable<Integer>, ComparableSyncItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Account() {
	}
	
	@Id
	@Column(name = "account_id")
	private int accountId;
	
	@Column(name = "account_name")
	private String accountName;
	
	@Column(name = "create_date", updatable = false, insertable = true)
	private Date createDate;
	
	@Column(name = "update_date")
	private Date updateDate;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "website")
	private String website;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "address")
	private String address;
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "logo_url", columnDefinition = "LONGBLOB")
	private byte[] logoUrl;
	
	@OneToMany(mappedBy = "accountId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@MapKey(name = "settingKey")
	private Map<String, ConfigSetting> settings = new HashMap<>();

	@Transient
	private boolean isNew = false;
	
	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLogoUrl() {
		if (logoUrl != null) {
			return Base64.getEncoder().encodeToString(logoUrl);
		}
		return null;
	}

	public void setLogoUrl(String base64String) {
		if (base64String != null && !base64String.isEmpty()) {
			this.logoUrl = Base64.getDecoder().decode(base64String);
		} else {
			this.logoUrl = null;
		}
	}

	public void setSettings(Map<String, ConfigSetting> settings) {
		this.settings = settings;
	}
	
	/**
	 * Get a simplified map of setting keys to their values only
	 * @return Map<String, String> where key is settingKey and value is settingValue
	 */
	public Map<String, String> getSettingsValues() {
		Map<String, String> settingsValues = new HashMap<>();
		for (Map.Entry<String, ConfigSetting> entry : settings.entrySet()) {
			settingsValues.put(entry.getKey(), entry.getValue().getSettingValue());
		}
		return settingsValues;
	}
	
	/**
	 * Get specific settings by their keys
	 * @param keys the setting keys to retrieve
	 * @return Map<String, String> containing only the requested settings
	 */
	public Map<String, String> getSpecificSettings(String... keys) {
		Map<String, String> specificSettings = new HashMap<>();
		for (String key : keys) {
			ConfigSetting setting = settings.get(key);
			if (setting != null) {
				specificSettings.put(key, setting.getSettingValue());
			}
		}
		return specificSettings;
	}
	
	/**
	 * Helper method to get a setting value by key
	 * @param key the setting key
	 * @return the setting value or null if not found
	 */
	public String getSettingValue(String key) {
		ConfigSetting setting = settings.get(key);
		return setting != null ? setting.getSettingValue() : null;
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
		return "Account [account_id="+this.accountId+", account_name=" + this.accountName + ", create_date=" + this.createDate
				+ ", update_date=" + this.updateDate + ", email=" + this.email + ", website=" + this.website
				+ ", phone=" + this.phone + ", address=" + this.address + ", logo_url=" + this.logoUrl + "]";
	}

	@Override
	public Integer getId() {
		return Integer.valueOf(this.getAccountId());
	}
	
}
