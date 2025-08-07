package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.ConfigSetting;

@Repository
public interface CloudConfigSettingRepositoryJPA extends JpaRepository<ConfigSetting, UUID> {

	Optional<ConfigSetting> findByIdAndAccountId(UUID id, int accountId);
	
}
