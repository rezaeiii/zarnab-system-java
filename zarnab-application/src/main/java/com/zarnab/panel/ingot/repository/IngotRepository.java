package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.Ingot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngotRepository extends JpaRepository<Ingot, Long> {
	List<Ingot> findByOwner_Id(Long ownerId);
} 