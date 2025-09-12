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
@Table(name = "subscription_plan_features")
@IdClass(SubscriptionPlanFeatureId.class)
public class SubscriptionPlanFeature implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @ManyToOne
    @JoinColumn(name = "sub_plan_id", referencedColumnName = "id")
	@JsonIgnore
    private SubscriptionPlan subscriptionPlan;

    @Id
    @Column(name = "feature_key")
    private String featureKey;

    @Column(name = "feature_value")
    private String featureValue;

    // Getters and setters
    public SubscriptionPlan getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    public String getFeatureKey() { return featureKey; }
    public void setFeatureKey(String featureKey) { this.featureKey = featureKey; }
    public String getFeatureValue() { return featureValue; }
    public void setFeatureValue(String featureValue) { this.featureValue = featureValue; }
}
