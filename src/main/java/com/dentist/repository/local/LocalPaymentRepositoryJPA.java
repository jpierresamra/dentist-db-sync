package com.dentist.repository.local;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Payment;

@Repository
public interface LocalPaymentRepositoryJPA extends JpaRepository<Payment, UUID> {

	Optional<Payment> findByIdAndAccountId(UUID id, int accountId);
}
