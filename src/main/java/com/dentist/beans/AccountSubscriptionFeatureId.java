package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;

public class AccountSubscriptionFeatureId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String accountSubscription;
	private String featureKey;

	public AccountSubscriptionFeatureId() {
	}

	public AccountSubscriptionFeatureId(String accountSubscription, String featureKey) {
		this.accountSubscription = accountSubscription;
		this.featureKey = featureKey;
	}

	public String getAccountSubscription() {
		return accountSubscription;
	}

	public void setAccountSubscription(String accountSubscription) {
		this.accountSubscription = accountSubscription;
	}

	public String getFeatureKey() {
		return featureKey;
	}

	public void setFeatureKey(String featureKey) {
		this.featureKey = featureKey;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AccountSubscriptionFeatureId that = (AccountSubscriptionFeatureId) o;
		return Objects.equals(accountSubscription, that.accountSubscription)
				&& Objects.equals(featureKey, that.featureKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountSubscription, featureKey);
	}
}
