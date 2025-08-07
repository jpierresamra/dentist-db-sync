package com.dentist.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dentist.repository.cloud.CloudAppointmentRepositoryJPA;
import com.dentist.repository.cloud.CloudCustomerRepositoryJPA;
import com.dentist.repository.cloud.CloudTreatmentRepositoryJPA;
import com.dentist.repository.local.LocalAppointmentRepositoryJPA;
import com.dentist.repository.local.LocalCustomerRepositoryJPA;
import com.dentist.repository.local.LocalTreatmentRepositoryJPA;
import com.dentist.service.DBSyncService;

@Service
public class DBSyncServiceImpl implements DBSyncService {

	private final CloudAppointmentRepositoryJPA cloudAppointmentRepository;
	private final LocalAppointmentRepositoryJPA localAppointmentRepository;

	private final CloudTreatmentRepositoryJPA cloudTreatmentRepository;
	private final LocalTreatmentRepositoryJPA localTreatmentRepository;
	
	private final CloudCustomerRepositoryJPA cloudCustomerRepository;
	private final LocalCustomerRepositoryJPA localCustomerRepository;

	@Value("${account.id}")
	private int accountId;

	public DBSyncServiceImpl(CloudAppointmentRepositoryJPA cloudAppointmentRepository,
			LocalAppointmentRepositoryJPA localAppointmentRepository,
			CloudTreatmentRepositoryJPA cloudTreatmentRepository,
			LocalTreatmentRepositoryJPA localTreatmentRepository,
			CloudCustomerRepositoryJPA cloudCustomerRepository,
			LocalCustomerRepositoryJPA localCustomerRepository) {
		this.cloudAppointmentRepository = cloudAppointmentRepository;
		this.localAppointmentRepository = localAppointmentRepository;
		this.cloudTreatmentRepository = cloudTreatmentRepository;
		this.localTreatmentRepository = localTreatmentRepository;
		this.cloudCustomerRepository = cloudCustomerRepository;
		this.localCustomerRepository = localCustomerRepository;
	}

	@Override
	public void synchronizeData() {
		/*synchronizeCustomers();
		synchronizeAppointments();
		synchronizeTreatments();*/
	}

	/*private void synchronizeTreatments() {
		// Get from the cloud all appointments related to this local account id
		List<Treatment> cloudTreatments = cloudTreatmentRepository.findByAccountId(this.accountId);
		List<Treatment> localTreatments = localTreatmentRepository.findAll();

		for (Treatment cloudEntity : cloudTreatments) {
			Treatment localEntity = localTreatmentRepository.findById(cloudEntity.getId()).orElse(null);
			if (localEntity == null) {
				cloudEntity.setNew(true);
				localTreatmentRepository.save(cloudEntity);
			} else {
				if (!localEntity.toString().equals(cloudEntity.toString())) {
					if (cloudEntity.getUpdateDate() != null && (localEntity == null
							|| cloudEntity.getUpdateDate().after(localEntity.getUpdateDate()))) {
						localTreatmentRepository.save(cloudEntity);
					}
				}
			}
		}

		// Loop on local entities
		for (Treatment localEntity : localTreatments) {
			Treatment cloudEntity = cloudTreatmentRepository.findById(localEntity.getId()).orElse(null);
			if (cloudEntity == null) {
				localEntity.setNew(true);
				cloudTreatmentRepository.save(localEntity);
			} else {
				if (!localEntity.toString().equals(cloudEntity.toString())) {
					if (localEntity.getUpdateDate() != null && (cloudEntity.getUpdateDate() == null
							|| localEntity.getUpdateDate().after(cloudEntity.getUpdateDate()))) {
						cloudTreatmentRepository.save(localEntity);
					}
				}
			}
		}
	}

	private void synchronizeAppointments() {
		// Get from the cloud all appointments related to this local account id
		List<Appointment> cloudAppointments = cloudAppointmentRepository.findByAccountId(this.accountId);
		List<Appointment> localAppointments = localAppointmentRepository.findAll();

		for (Appointment cloudEntity : cloudAppointments) {
			Appointment localEntity = localAppointmentRepository.findById(cloudEntity.getId()).orElse(null);
			if (localEntity == null) {
				cloudEntity.setNew(true);
				localAppointmentRepository.save(cloudEntity);
			} else {
				if (!localEntity.toString().equals(cloudEntity.toString())) {
					if (cloudEntity.getUpdateDate() != null && (localEntity == null
							|| cloudEntity.getUpdateDate().after(localEntity.getUpdateDate()))) {
						localAppointmentRepository.save(cloudEntity);
					}
				}
			}
		}

		// Loop on local entities
		for (Appointment localEntity : localAppointments) {
			Appointment cloudEntity = cloudAppointmentRepository.findById(localEntity.getId()).orElse(null);
			if (cloudEntity == null) {
				localEntity.setNew(true);
				cloudAppointmentRepository.save(localEntity);
			} else {
				if (!localEntity.toString().equals(cloudEntity.toString())) {
					if (localEntity.getUpdateDate() != null && (cloudEntity.getUpdateDate() == null
							|| localEntity.getUpdateDate().after(cloudEntity.getUpdateDate()))) {
						cloudAppointmentRepository.save(localEntity);
					}
				}
			}
		}
	}
	
	private void synchronizeCustomers() {
		// Get from the cloud all customers related to this local account id
		List<Customer> cloudCustomers = cloudCustomerRepository.findByAccountId(this.accountId);
		List<Customer> localCustomers = localCustomerRepository.findAll();

		for (Customer cloudEntity : cloudCustomers) {
			Customer localEntity = localCustomerRepository.findById(cloudEntity.getId()).orElse(null);
			if (localEntity == null) {
				cloudEntity.setNew(true);
				localCustomerRepository.save(cloudEntity);
			} else {
				if (!localEntity.toString().equals(cloudEntity.toString())) {
					if (cloudEntity.getUpdateDate() != null && (localEntity == null
							|| cloudEntity.getUpdateDate().after(localEntity.getUpdateDate()))) {
						localCustomerRepository.save(cloudEntity);
					}
				}
			}
		}

		// Loop on local entities
		for (Customer localEntity : localCustomers) {
			Customer cloudEntity = cloudCustomerRepository.findById(localEntity.getId()).orElse(null);
			if (cloudEntity == null) {
				localEntity.setNew(true);
				cloudCustomerRepository.save(localEntity);
			} else {
				if (!localEntity.toString().equals(cloudEntity.toString())) {
					if (localEntity.getUpdateDate() != null && (cloudEntity.getUpdateDate() == null
							|| localEntity.getUpdateDate().after(cloudEntity.getUpdateDate()))) {
						cloudCustomerRepository.save(localEntity);
					}
				}
			}
		}
	}*/
}
