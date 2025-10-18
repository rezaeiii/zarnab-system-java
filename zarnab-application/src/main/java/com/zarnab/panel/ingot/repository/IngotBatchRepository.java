package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.IngotBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngotBatchRepository extends JpaRepository<IngotBatch, Long> {
}
