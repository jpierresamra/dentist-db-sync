package com.dentist.repository.local;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.MedicalSheet;

@Repository
public interface LocalMedicalSheetRepositoryJPA extends JpaRepository<MedicalSheet, UUID> {
	Optional<MedicalSheet> findByIdAndAccountId(UUID id, int accountId);
}
