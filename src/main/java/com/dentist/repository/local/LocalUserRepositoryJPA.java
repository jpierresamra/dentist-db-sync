package com.dentist.repository.local;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.User;

@Repository
public interface LocalUserRepositoryJPA extends JpaRepository<User, UUID> {

	Optional<User> findByIdAndAccountId(UUID id, int accountId);
	
}
