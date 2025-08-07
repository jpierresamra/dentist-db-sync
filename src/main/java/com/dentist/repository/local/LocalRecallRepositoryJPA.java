package com.dentist.repository.local;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Recall;

@Repository
public interface LocalRecallRepositoryJPA extends JpaRepository<Recall, UUID> {
    // Standard JPA repository methods are sufficient for local database
    // since it's single-tenant (only one account)
	
	Optional<Recall> findByIdAndAccountId(UUID recallId, int accountId);
}
