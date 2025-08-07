package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentist.beans.MedicalSheet;

@Repository
public interface CloudMedicalSheetRepositoryJPA extends JpaRepository<MedicalSheet, UUID> {

    Optional<MedicalSheet> findByIdAndAccountId(UUID id, int accountId);

    /**
     * Check if medical sheet exists for the specified account
     * @param id the medical sheet ID
     * @param accountId the account ID
     * @return true if medical sheet exists for the account
     */
    @Query("SELECT COUNT(m) > 0 FROM MedicalSheet m WHERE m.id = :id AND m.accountId = :accountId")
    boolean existsByIdAndAccountId(@Param("id") UUID id, @Param("accountId") int accountId);
}
