package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class AccountSubscriptionQuotaId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID accountSubscription;
    private Integer quotaType;

    public AccountSubscriptionQuotaId() {}
    
    public AccountSubscriptionQuotaId(UUID accountSubscription, Integer quotaType) {
        this.accountSubscription = accountSubscription;
        this.quotaType = quotaType;
    }

    public UUID getAccountSubscription() {
		return accountSubscription;
	}

	public void setAccountSubscription(UUID accountSubscription) {
		this.accountSubscription = accountSubscription;
	}

	public Integer getQuotaType() { 
        return quotaType; 
    }
    
    public void setQuotaType(Integer quotaType) { 
        this.quotaType = quotaType; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountSubscriptionQuotaId that = (AccountSubscriptionQuotaId) o;
        return Objects.equals(accountSubscription, that.accountSubscription) && 
               Objects.equals(quotaType, that.quotaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountSubscription, quotaType);
    }
}
