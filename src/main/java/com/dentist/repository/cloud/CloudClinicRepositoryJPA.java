package com.dentist.repository.cloud;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Clinic;

@Repository
public interface CloudClinicRepositoryJPA extends JpaRepository<Clinic, UUID> {

    @Query("SELECT c FROM Clinic c WHERE c.accountId = :accountId AND c.status != 2")
    List<Clinic> findByAccountId(@Param("accountId") int accountId);

    Optional<Clinic> findByClinicIdAndAccountId(UUID clinicId, int accountId);

    @Query("SELECT c FROM Clinic c WHERE c.accountId = :accountId AND c.status != 2 AND " +
           "(:filter IS NULL OR :filter = '' OR LOWER(c.clinicName) LIKE LOWER(CONCAT('%', :filter, '%')))")
    List<Clinic> findByFilter(@Param("filter") String filter, Pageable pageable, @Param("accountId") int accountId);
}
