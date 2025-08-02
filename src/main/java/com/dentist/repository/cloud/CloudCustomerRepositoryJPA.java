package com.dentist.repository.cloud;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Appointment;
import com.dentist.beans.Customer;

@Repository
public interface CloudCustomerRepositoryJPA extends JpaRepository<Customer, UUID> {
    // Fetch all customers that have account id
    List<Customer> findByAccountId(int accountId);
}