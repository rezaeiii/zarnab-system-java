package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.repository.IngotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngotServiceImpl implements IngotService {

    private final IngotRepository ingotRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IngotResponse> list(User requester) {
        boolean isAdmin = requester.getRoles().stream().anyMatch(role -> role.equals(Role.ADMIN));
        List<Ingot> ingots = isAdmin
                ? ingotRepository.findAll()
                : ingotRepository.findByOwnerId(requester.getId());
        return ingots.stream().map(IngotResponse::from).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IngotResponse create(IngotCreateRequest request) {
        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));
        Ingot ingot = Ingot.builder()
                .serial(request.serial())
                .manufactureDate(request.manufactureDate())
                .karat(request.karat())
                .weightGrams(request.weightGrams())
                .owner(owner)
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
}