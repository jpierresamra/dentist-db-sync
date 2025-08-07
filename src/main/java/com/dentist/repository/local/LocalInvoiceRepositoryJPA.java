package com.dentist.repository.local;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Invoice;

@Repository
public interface LocalInvoiceRepositoryJPA extends JpaRepository<Invoice, UUID> {

    /**
     * Find invoice by ID and account ID for security
     */
    Optional<Invoice> findByIdAndAccountId(UUID id, int accountId);
}
