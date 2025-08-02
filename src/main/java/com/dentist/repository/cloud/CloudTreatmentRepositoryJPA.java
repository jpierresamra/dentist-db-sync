package com.dentist.repository.cloud;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Treatment;

@Repository
public interface CloudTreatmentRepositoryJPA extends JpaRepository<Treatment, UUID> {
    // Fetch all treatments that have account id
    List<Treatment> findByAccountId(int accountId);
}