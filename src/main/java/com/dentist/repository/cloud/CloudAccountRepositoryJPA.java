package com.dentist.repository.cloud;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Account;

@Repository
public interface CloudAccountRepositoryJPA extends JpaRepository<Account, Integer> {

    Optional<Account> findByAccountId(int accountId);

    /**
     * Check if account exists
     * @param accountId the account ID
     * @return true if account exists
     */
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.accountId = :accountId")
    boolean existsByAccountId(@Param("accountId") int accountId);
}
