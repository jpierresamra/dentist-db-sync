package com.dentist.repository.local;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.ConfigSetting;

@Repository
public interface LocalConfigSettingRepositoryJPA extends JpaRepository<ConfigSetting, UUID> {

    Optional<ConfigSetting> findByIdAndAccountId(UUID id, int accountId);
}
