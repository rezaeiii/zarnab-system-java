package com.zarnab.panel.ingot.repository;

import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.ReportIssue;
import com.zarnab.panel.ingot.model.ReportIssueStatus;
import com.zarnab.panel.ingot.model.ReportIssueType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReportIssueRepository extends JpaRepository<ReportIssue, Long>, JpaSpecificationExecutor<ReportIssue> {
    boolean existsByIngotAndStatusIn(Ingot ingot, List<ReportIssueStatus> statuses);

    boolean existsByIngotAndTypeAndStatusIn(Ingot ingot, ReportIssueType type, List<ReportIssueStatus> statuses);

    List<ReportIssue> findAllByReporterId(Long reporterId);
}
