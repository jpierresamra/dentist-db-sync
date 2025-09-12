package com.dentist.beans;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class AccountSubscriptionModuleId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UUID accountSubscription;
    private Integer module;

    public AccountSubscriptionModuleId() {}
    
    public AccountSubscriptionModuleId(UUID accountSubscription, Integer module) {
        this.accountSubscription = accountSubscription;
        this.module = module;
    }

    public UUID getAccountSubscription() {
		return accountSubscription;
	}

	public void setAccountSubscription(UUID accountSubscription) {
		this.accountSubscription = accountSubscription;
	}

	public Integer getModule() { 
        return module; 
    }
    
    public void setModule(Integer module) { 
        this.module = module; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountSubscriptionModuleId that = (AccountSubscriptionModuleId) o;
        return Objects.equals(accountSubscription, that.accountSubscription) && 
               Objects.equals(module, that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountSubscription, module);
    }
}
