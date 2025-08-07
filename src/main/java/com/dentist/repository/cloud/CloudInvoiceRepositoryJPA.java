package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Invoice;

@Repository
public interface CloudInvoiceRepositoryJPA extends JpaRepository<Invoice, UUID> {

    /**
     * Find invoice by ID and account ID for security
     */
    Optional<Invoice> findByIdAndAccountId(UUID id, int accountId);
}
