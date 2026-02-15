package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.search.SpecificationBuilder;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.ingot.dto.req.BatchCreateRequest;
import com.zarnab.panel.ingot.dto.req.BulkIngotStateChangeRequest;
import com.zarnab.panel.ingot.dto.res.BatchCreateResponse;
import com.zarnab.panel.ingot.dto.res.BatchIngotResponse;
import com.zarnab.panel.ingot.model.*;
import com.zarnab.panel.ingot.repository.IngotBatchRepository;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.repository.TransferRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngotServiceImpl implements IngotService {

    private final IngotRepository ingotRepository;
    private final IngotBatchRepository ingotBatchRepository;
    private final TransferRepository transferRepository;

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<IngotResponse> list(User requester, PageableRequest pageableRequest) {

        boolean justActives = justNeedActives(pageableRequest);
        boolean justZarnabOwners = justZarnabOwners(pageableRequest);

        Specification<Ingot> spec = SpecificationBuilder.buildSpecification(pageableRequest);

        if (RoleUtil.hasRole(requester, Role.CUSTOMER)) {
            Specification<Ingot> customerSpec = (root, query, cb) -> cb.equal(root.get("owner"), requester);
            spec = (spec == null) ? customerSpec : spec.and(customerSpec);
        } else if (RoleUtil.hasRole(requester, Role.COUNTER)) {
            Specification<Ingot> counterSpec = (root, query, cb) -> cb.or(
                    cb.equal(root.get("owner"), requester)
            );
            spec = (spec == null) ? counterSpec : spec.and(counterSpec);
        } else if (RoleUtil.hasRole(requester, Role.ADMIN)) {
            if (justZarnabOwners) {
                Specification<Ingot> counterSpec = (root, query, cb) -> cb.or(
                        cb.isNull(root.get("owner"))
                );
                spec = (spec == null) ? counterSpec : spec.and(counterSpec);
            }
        }
        if (justActives) {
            Specification<Ingot> activeSpec = (root, query, cb) -> {
                Subquery<ReportIssue> subquery = query.subquery(ReportIssue.class);
                Root<ReportIssue> subRoot = subquery.from(ReportIssue.class);
                subquery.select(subRoot);

                Predicate ingotPredicate = cb.equal(subRoot.get("ingot"), root);
                Predicate statusPredicate = subRoot.get("status").in(ReportIssueStatus.APPROVED, ReportIssueStatus.PENDING);
                subquery.where(cb.and(ingotPredicate, statusPredicate));

                return cb.not(cb.exists(subquery));
            };
            spec = (spec == null) ? activeSpec : spec.and(activeSpec);
        }

        Specification<Ingot> stateSpec = (root, query, cb) -> cb.or(
                cb.equal(root.get("state"), IngotState.ASSIGNED)
        );
        spec = (spec == null) ? stateSpec : spec.and(stateSpec);

        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), pageableRequest.getSort());
        Page<Ingot> ingotPage = ingotRepository.findAll(spec, pageable);

        List<IngotResponse> responses = ingotPage.getContent().stream()
                .map(IngotResponse::from)
                .collect(Collectors.toList());

        return new PageableResponse<>(responses, ingotPage.getTotalElements(), ingotPage.getNumber(), ingotPage.getSize());
    }


    @Override
    @Transactional
    public IngotResponse create(IngotCreateRequest request) {
        if (ingotRepository.existsBySerial(request.serial())) {
            throw new ZarnabException(ExceptionType.INGOT_ALREADY_EXISTS, request.serial());
        }

        Ingot ingot = Ingot.builder()
                .serial(request.serial())
                .manufactureDate(request.manufactureDate())
                .karat(request.karat())
                .weightGrams(request.weightGrams())
                .state(IngotState.ASSIGNED) // Assuming manual creation is for assigned ingots
                .build();
        Ingot saved = ingotRepository.save(ingot);
        return IngotResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public IngotResponse inquiry(String serial) {
        return ingotRepository.findBySerial(serial)
                .map(IngotResponse::from)
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));
    }

    @Override
    @Transactional
    public BatchCreateResponse createBatch(BatchCreateRequest request) {
        LocalDate date = request.manufactureDate();
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        int lastSequence = ingotRepository.findTopBymanufactureDateBetweenOrderByIdDesc(startOfMonth, endOfMonth)
                .map(ingot -> Integer.parseInt(ingot.getSerial().substring(6)))
                .orElse(0);

        IngotBatch batch = IngotBatch.builder()
                .manufactureDate(date)
                .build();
        batch = ingotBatchRepository.save(batch);

        List<Ingot> newIngots = new ArrayList<>();
        List<String> serials = new ArrayList<>();
        for (int i = 0; i < request.count(); i++) {
            lastSequence++;
            String serial = generateSerial(request.productType().getCode(), request.purity().getCode(), date, lastSequence);
            serials.add(serial);

            Ingot ingot = Ingot.builder()
                    .serial(serial)
                    .manufactureDate(date)
                    .karat(Integer.parseInt(request.purity().name().substring(1)))
                    .weightGrams(request.weight())
                    .state(IngotState.GENERATED)
                    .batch(batch)
                    .build();
            newIngots.add(ingot);
        }

        ingotRepository.saveAll(newIngots);
        return new BatchCreateResponse(batch.getId(), serials);
    }

    private String generateSerial(String productCode, String purityCode, LocalDate date, int sequence) {
        String year = String.valueOf(date.getYear()).substring(2);
        String month = String.format("%02d", date.getMonthValue());
        String serialNum = String.format("%04d", sequence);
        return productCode.toUpperCase() + purityCode.toUpperCase() + year + month + serialNum;
    }

    @Override
    @Transactional(readOnly = true)
    public String getBatchCsv(Long batchId, String baseUrl) {
        List<Ingot> ingots = ingotRepository.findByBatchIdOrderByIdAsc(batchId);
        if (ingots.isEmpty()) {
            return "";
        }
        return ingots.stream()
                .map(ingot -> baseUrl + "/public-inquiry/" + ingot.getSerial())
                .collect(Collectors.joining("\n"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BatchIngotResponse> getBatchIngots(Long batchId) {
        List<Ingot> ingots = ingotRepository.findByBatchIdOrderByIdAsc(batchId);
        Set<Long> transferredIngotIds = transferRepository.findTransferredIngotIdsByBatchId(batchId);

        return ingots.stream()
                .map(ingot -> BatchIngotResponse.from(ingot, transferredIngotIds.contains(ingot.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IngotResponse assignIngot(String serial) {
        Ingot ingot = ingotRepository.findBySerial(serial)
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));

        if (transferRepository.existsByIngotId(ingot.getId())) {
            throw new ZarnabException(ExceptionType.INGOT_NOT_ASSIGNABLE);
        }

        if (ingot.getState() != IngotState.GENERATED) {
            return IngotResponse.from(ingot);
        }

        ingot.setState(IngotState.ASSIGNED);
        Ingot saved = ingotRepository.save(ingot);
        return IngotResponse.from(saved);
    }

    @Override
    @Transactional
    public IngotResponse unassignIngot(String serial) {
        Ingot ingot = ingotRepository.findBySerial(serial)
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));

        if (ingot.getState() != IngotState.ASSIGNED) {
            return IngotResponse.from(ingot);
        }

        ingot.setState(IngotState.GENERATED);
        Ingot saved = ingotRepository.save(ingot);
        return IngotResponse.from(saved);
    }

    @Override
    @Transactional
    public void bulkStateChange(BulkIngotStateChangeRequest request) {
        List<Ingot> ingots = ingotRepository.findAllBySerialIn(request.serials());
        if (ingots.size() != request.serials().size()) {
            throw new ZarnabException(ExceptionType.INGOT_NOT_FOUND);
        }

        for (Ingot ingot : ingots) {
            ingot.setState(request.assign() ? IngotState.ASSIGNED : IngotState.GENERATED);
        }

        ingotRepository.saveAll(ingots);
    }


    @Override
    @Transactional(readOnly = true)
    public PageableResponse<IngotDtos.IngotBatchResponse> listBatches(PageableRequest pageableRequest) {
        Specification<IngotBatch> spec = SpecificationBuilder.buildSpecification(pageableRequest);
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), pageableRequest.getSort());
        Page<IngotBatch> ingotBatchPage = ingotBatchRepository.findAll(spec, pageable);

        List<IngotDtos.IngotBatchResponse> responses = ingotBatchPage.getContent().stream()
                .map(IngotDtos.IngotBatchResponse::from)
                .collect(Collectors.toList());

        return new PageableResponse<>(responses, ingotBatchPage.getTotalElements(), ingotBatchPage.getNumber(), ingotBatchPage.getSize());
    }

    private boolean justNeedActives(PageableRequest pageableRequest) {
        boolean justActives = pageableRequest.getFilters()
                .stream()
                .filter(f -> f.getField().equalsIgnoreCase("active"))
                .anyMatch(f -> f.getValue().equalsIgnoreCase("true"));
        pageableRequest.getFilters().removeIf(f -> f.getField().equalsIgnoreCase("active"));
        return justActives;
    }


    private boolean justZarnabOwners(PageableRequest pageableRequest) {
        boolean justActives = pageableRequest.getFilters()
                .stream()
                .filter(f -> f.getField().equalsIgnoreCase("justMyOwners"))
                .anyMatch(f -> f.getValue().equalsIgnoreCase("true"));
        pageableRequest.getFilters().removeIf(f -> f.getField().equalsIgnoreCase("justMyOwners"));
        return justActives;
    }

}
