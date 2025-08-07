package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Recall;

@Repository
public interface CloudRecallRepositoryJPA extends JpaRepository<Recall, UUID> {

	Optional<Recall> findByIdAndAccountId(UUID recallId, int accountId);

    /**
     * Check if recall exists for the specified account
     * @param id the recall ID
     * @param accountId the account ID
     * @return true if recall exists for the account
     */
    @Query("SELECT COUNT(r) > 0 FROM Recall r WHERE r.id = :id AND r.accountId = :accountId")
    boolean existsByIdAndAccountId(@Param("id") UUID id, @Param("accountId") int accountId);
}
