package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Procedure;

@Repository
public interface CloudProcedureRepositoryJPA extends JpaRepository<Procedure, UUID> {

    Optional<Procedure> findByIdAndAccountId(UUID id, int accountId);

    /**
     * Check if procedure exists for the specified account
     * @param id the procedure ID
     * @param accountId the account ID
     * @return true if procedure exists for the account
     */
    @Query("SELECT COUNT(p) > 0 FROM Procedure p WHERE p.id = :id AND p.accountId = :accountId")
    boolean existsByIdAndAccountId(@Param("id") UUID id, @Param("accountId") int accountId);
}
