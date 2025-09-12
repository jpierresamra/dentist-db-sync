package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.ConfigClinicSetting;

@Repository
public interface CloudConfigClinicSettingsRepositoryJPA extends JpaRepository<ConfigClinicSetting, UUID> {

	Optional<ConfigClinicSetting> findAllByClinicIdAndAccountId(UUID clinicId, int accountId);
}
