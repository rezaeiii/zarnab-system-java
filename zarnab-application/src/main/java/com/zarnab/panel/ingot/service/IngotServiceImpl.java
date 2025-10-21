package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.ingot.dto.req.BatchCreateRequest;
import com.zarnab.panel.ingot.dto.res.BatchCreateResponse;
import com.zarnab.panel.ingot.dto.res.BatchIngotResponse;
import com.zarnab.panel.ingot.dto.res.IngotBatchResponse;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.IngotBatch;
import com.zarnab.panel.ingot.model.IngotState;
import com.zarnab.panel.ingot.repository.IngotBatchRepository;
import com.zarnab.panel.ingot.repository.IngotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngotServiceImpl implements IngotService {

    private final IngotRepository ingotRepository;
    private final IngotBatchRepository ingotBatchRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IngotResponse> list(User requester) {
        boolean isAdmin = RoleUtil.hasRole(requester, Role.ADMIN);
        boolean isCounter = RoleUtil.hasRole(requester, Role.COUNTER);
        List<Ingot> ingots;
        if (isAdmin) {
            ingots = ingotRepository.findAll();
        } else if (isCounter) {
            ingots = ingotRepository.findByOwnerIdOrOwnerIdIsNull(requester.getId());
        } else {
            ingots = ingotRepository.findByOwnerId(requester.getId());
        }
        return ingots.stream().map(IngotResponse::from).collect(Collectors.toList());
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

        int lastSequence = ingotRepository.findTopBymanufactureDateBetweenOrderBySerialDesc(startOfMonth, endOfMonth)
                .map(ingot -> Integer.parseInt(ingot.getSerial().substring(8)))
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
    public String getBatchCsv(Long batchId) {
        IngotBatch batch = ingotBatchRepository.findById(batchId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.RESOURCE_NOT_FOUND, batchId));

        return batch.getIngots().stream()
                .map(Ingot::getSerial)
                .collect(Collectors.joining("\n"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BatchIngotResponse> getBatchIngots(Long batchId) {
        IngotBatch batch = ingotBatchRepository.findById(batchId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.RESOURCE_NOT_FOUND, batchId));

        return batch.getIngots().stream()
                .map(BatchIngotResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public IngotResponse assignIngot(String serial) {
        Ingot ingot = ingotRepository.findBySerial(serial)
                .orElseThrow(() -> new ZarnabException(ExceptionType.INGOT_NOT_FOUND));

        if (ingot.getState() != IngotState.GENERATED) {
            return IngotResponse.from(ingot);
        }

        ingot.setState(IngotState.ASSIGNED);
        Ingot saved = ingotRepository.save(ingot);
        return IngotResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IngotBatchResponse> listBatches() {
        return ingotBatchRepository.findAll().stream()
                .map(batch -> new IngotBatchResponse(
                        batch.getId(),
                        batch.getManufactureDate(),
                        batch.getIngots() != null ? batch.getIngots().size() : 0
                ))
                .collect(Collectors.toList());
    }
}
