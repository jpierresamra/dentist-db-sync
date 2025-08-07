package com.dentist.repository.local;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentist.beans.ClassType;

@Repository
public interface LocalClassTypeRepositoryJPA extends JpaRepository<ClassType, UUID> {
    // Standard JPA repository methods are sufficient for local database
    // since it's single-tenant (only one account)
	
	Optional<ClassType> findByIdAndAccountId(UUID id, int accountId);
}
