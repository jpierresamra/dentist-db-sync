package com.dentist.repository.local;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.MedicalSheet;

@Repository
public interface LocalMedicalSheetRepositoryJPA extends JpaRepository<MedicalSheet, UUID> {
    // Standard JPA repository methods are sufficient for local database
    // since it's single-tenant (only one account)
	Optional<MedicalSheet> findByIdAndAccountId(UUID id, int accountId);
}
