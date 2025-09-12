package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;

public class SubscriptionPlanFeatureId implements Serializable {
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer subscriptionPlan;
    private String featureKey;

    public SubscriptionPlanFeatureId() {}
    public SubscriptionPlanFeatureId(Integer subscriptionPlan, String featureKey) {
        this.subscriptionPlan = subscriptionPlan;
        this.featureKey = featureKey;
    }
    public Integer getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(Integer subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    public String getFeatureKey() { return featureKey; }
    public void setFeatureKey(String featureKey) { this.featureKey = featureKey; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionPlanFeatureId that = (SubscriptionPlanFeatureId) o;
        return Objects.equals(subscriptionPlan, that.subscriptionPlan) && Objects.equals(featureKey, that.featureKey);
    }
    @Override
    public int hashCode() {
        return Objects.hash(subscriptionPlan, featureKey);
    }
}
