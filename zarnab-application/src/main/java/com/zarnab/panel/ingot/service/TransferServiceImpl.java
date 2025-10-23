package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.dto.UserManagementDtos;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.auth.service.sms.SmsService;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.search.SpecificationBuilder;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.core.util.RoleUtil;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.req.InitiateTransferRequest;
import com.zarnab.panel.ingot.dto.req.VerifyTransferRequest;
import com.zarnab.panel.ingot.dto.res.InitiateTransferResponse;
import com.zarnab.panel.ingot.model.Ingot;
import com.zarnab.panel.ingot.model.TheftReportStatus;
import com.zarnab.panel.ingot.model.Transfer;
import com.zarnab.panel.ingot.model.TransferStatus;
import com.zarnab.panel.ingot.repository.IngotRepository;
import com.zarnab.panel.ingot.repository.TheftReportRepository;
import com.zarnab.panel.ingot.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.zarnab.panel.common.translate.Translator.translate;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final IngotRepository ingotRepository;
    private final UserRepository userRepository;
    private final TransferRepository transferRepository;
    private final TheftReportRepository theftReportRepository;
    private final OtpService otpService;
    private final SmsService smsService;

    @Override
    @Transactional
    public InitiateTransferResponse initiateTransfer(InitiateTransferRequest request, String username) {
        // ... existing implementation ...
        return null;
    }

    @Override
    @Transactional
    public IngotDtos.TransferDto verifyTransfer(VerifyTransferRequest request, String username) {
        // ... existing implementation ...
        return null;
    }

    @Override
    @Transactional
    public void cancelTransfer(Long transferId, String username) {
        // ... existing implementation ...
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<IngotDtos.TransferDto> getTransfers(User user, PageableRequest pageableRequest) {
        Specification<Transfer> spec = SpecificationBuilder.buildSpecification(pageableRequest);

        boolean isAdminOrCounter = RoleUtil.hasRole(user, Role.ADMIN, Role.COUNTER);
        if (!isAdminOrCounter) {
            Specification<Transfer> userSecuritySpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("seller"), user),
                            criteriaBuilder.equal(root.get("buyer"), user)
                    );
            spec = (spec == null) ? userSecuritySpec : spec.and(userSecuritySpec);
        }

        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), pageableRequest.getSort());

        Page<Transfer> transferPage = transferRepository.findAll(spec, pageable);

        List<IngotDtos.TransferDto> transferDtos = transferPage.getContent().stream()
                .map(IngotDtos.TransferDto::from)
                .collect(Collectors.toList());

        return new PageableResponse<>(
                transferDtos,
                transferPage.getTotalElements(),
                transferPage.getNumber(),
                transferPage.getSize()
        );
    }
}
