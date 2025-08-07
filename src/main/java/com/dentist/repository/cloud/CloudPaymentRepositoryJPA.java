package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Payment;

@Repository
public interface CloudPaymentRepositoryJPA extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByIdAndAccountId(UUID id, int accountId);

    /**
     * Check if payment exists for the specified account
     * @param id the payment ID
     * @param accountId the account ID
     * @return true if payment exists for the account
     */
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.id = :id AND p.accountId = :accountId")
    boolean existsByIdAndAccountId(@Param("id") UUID id, @Param("accountId") int accountId);
}
