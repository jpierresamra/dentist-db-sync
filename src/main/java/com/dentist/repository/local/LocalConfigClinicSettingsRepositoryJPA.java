package com.dentist.repository.local;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.ConfigClinicSetting;

@Repository
public interface LocalConfigClinicSettingsRepositoryJPA extends JpaRepository<ConfigClinicSetting, UUID> {

	Optional<ConfigClinicSetting> findAllByClinicIdAndAccountId(UUID clinicId, int accountId);
}
