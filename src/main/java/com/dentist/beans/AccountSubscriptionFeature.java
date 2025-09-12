package com.dentist.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "account_subscription_features")
@IdClass(AccountSubscriptionFeatureId.class)
public class AccountSubscriptionFeature implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @ManyToOne
    @JoinColumn(name = "account_subscription_id", referencedColumnName = "id")
	@JsonIgnore
    private AccountSubscription accountSubscription;

    @Id
    @Column(name = "feature_key")
    private String featureKey;

    @Column(name = "feature_value")
    private String featureValue;

	public AccountSubscription getAccountSubscription() {
		return accountSubscription;
	}

	public void setAccountSubscription(AccountSubscription accountSubscription) {
		this.accountSubscription = accountSubscription;
	}

	public String getFeatureKey() {
		return featureKey;
	}

	public void setFeatureKey(String featureKey) {
		this.featureKey = featureKey;
	}

	public String getFeatureValue() {
		return featureValue;
	}

	public void setFeatureValue(String featureValue) {
		this.featureValue = featureValue;
	}


}
