package com.dentist.repository.local;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Appointment;

@Repository
public interface LocalAppointmentRepositoryJPA extends JpaRepository<Appointment, UUID> {
    // Fetch all appointments
    List<Appointment> findAll();
}