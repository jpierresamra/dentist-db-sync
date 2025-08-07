package com.dentist.repository.local;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Account;

@Repository
public interface LocalAccountRepositoryJPA extends JpaRepository<Account, Integer> {
    // Standard JPA repository methods are sufficient for local database
    // since it's single-tenant (only one account)
	
	Optional<Account> findByAccountId(int accountId);
}
