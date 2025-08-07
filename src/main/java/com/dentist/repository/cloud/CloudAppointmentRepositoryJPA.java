package com.dentist.repository.cloud;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Appointment;

@Repository
public interface CloudAppointmentRepositoryJPA extends JpaRepository<Appointment, UUID> {
    // Fetch all appointments that have account id
    List<Appointment> findByAccountId(int accountId);
    
    Optional<Appointment> findByIdAndAccountId(UUID appointmentId, int accountId);
}