package com.dentist.repository.local;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dentist.beans.Tooth;

@Repository
public interface LocalToothRepositoryJPA extends JpaRepository<Tooth, Integer> {
	
	Iterable<Tooth> findAllByOrderByToothOrderAsc();
	
	Optional<Tooth> findById(Integer id);
	
	Iterable<Tooth> findByToothGroupOrderByToothOrderAsc(String toothGroup);
	
}
