package com.dentist.repository.cloud;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dentist.beans.ConfigAccountSetting;

@Repository
public interface CloudConfigAccountSettingsRepositoryJPA extends JpaRepository<ConfigAccountSetting, UUID> {

	Optional<ConfigAccountSetting> findByIdAndAccountId(UUID id, int accountId);
}
