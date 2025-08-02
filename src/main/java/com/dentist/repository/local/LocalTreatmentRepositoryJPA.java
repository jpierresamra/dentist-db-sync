package com.dentist.repository.local;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Treatment;

@Repository
public interface LocalTreatmentRepositoryJPA extends JpaRepository<Treatment, UUID> {
    // Fetch all treatments
    List<Treatment> findAll();
}