package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Operation;

@Repository
public interface CloudOperationRepositoryJPA extends JpaRepository<Operation, UUID> {

	Optional<Operation> findByIdAndAccountId(UUID id, int accountId);
}
