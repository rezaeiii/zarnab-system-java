package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.TheftReport;
import com.zarnab.panel.ingot.model.TheftReportStatus;
import com.zarnab.panel.ingot.model.TheftReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TheftReportRepository extends JpaRepository<TheftReport, Long> {
    boolean existsByIngotAndStatusIn(Ingot ingot, List<TheftReportStatus> statuses);

    boolean existsByIngotAndTypeAndStatusIn(Ingot ingot, TheftReportType type, List<TheftReportStatus> statuses);

    List<TheftReport> findAllByReporterId(Long reporterId);

}
