package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Treatment;

@Repository
public interface CloudTreatmentRepositoryJPA extends JpaRepository<Treatment, UUID> {
	
    Optional<Treatment> findByIdAndAccountId(UUID id, int accountId);
}