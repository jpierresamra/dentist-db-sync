package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentist.beans.ClassType;

@Repository
public interface CloudClassTypeRepositoryJPA extends JpaRepository<ClassType, UUID> {

	Optional<ClassType> findByIdAndAccountId(UUID id, int accountId);

    /**
     * Check if class type exists for the specified account
     * @param id the class type ID
     * @param accountId the account ID
     * @return true if class type exists for the account
     */
    @Query("SELECT COUNT(c) > 0 FROM ClassType c WHERE c.id = :id AND c.accountId = :accountId")
    boolean existsByIdAndAccountId(@Param("id") UUID id, @Param("accountId") int accountId);
}
