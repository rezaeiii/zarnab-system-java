package com.zarnab.panel.inheritance.service;

import com.zarnab.panel.auth.model.AccountStatus;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.auth.repository.UserRepository;
import com.zarnab.panel.auth.service.otp.OtpPurpose;
import com.zarnab.panel.auth.service.otp.OtpService;
import com.zarnab.panel.auth.service.sms.SmsService;
import com.zarnab.panel.clients.dto.FlatPersonInquiryResponse;
import com.zarnab.panel.clients.service.PersonInquiryClient;
import com.zarnab.panel.clients.service.ShahkarInquiryClient;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.common.file.service.FileStorageService;
import com.zarnab.panel.common.search.SpecificationBuilder;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.core.exception.ExceptionType;
import com.zarnab.panel.inheritance.dto.InheritanceDtos;
import com.zarnab.panel.inheritance.model.Heir;
import com.zarnab.panel.inheritance.model.HeirDocumentStatus;
import com.zarnab.panel.inheritance.model.InheritanceCase;
import com.zarnab.panel.inheritance.model.InheritanceStatus;
import com.zarnab.panel.inheritance.repository.HeirRepository;
import com.zarnab.panel.inheritance.repository.InheritanceCaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.zarnab.panel.common.translate.Translator.translate;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InheritanceServiceImpl implements InheritanceService {

    private final InheritanceCaseRepository caseRepository;
    private final HeirRepository heirRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final SmsService smsService;
    private final PersonInquiryClient personInquiryClient;
    private final ShahkarInquiryClient shahkarInquiryClient;
    private final OtpService otpService;

    @Override
    @Transactional(readOnly = true)
    public InheritanceDtos.DeathVerificationDetailResponse verifyDeathStatus(InheritanceDtos.DeathVerificationRequest request) {

        // Verify requester's mobile number with Shahkar
        Boolean isMobileOwner = shahkarInquiryClient.verifyMobileOwner(request.requesterNationalId(), request.requesterMobileNumber()).block();
        if (isMobileOwner == null || !isMobileOwner) {
            throw new ZarnabException(ExceptionType.INVALID_MOBILE_NATIONAL_SHAHKAR);
        }

        // Get requester's info
        FlatPersonInquiryResponse requesterInfo = personInquiryClient.getPersonInfo(request.requesterNationalId(), request.requesterBirthDate()).block();
//        FlatPersonInquiryResponse requesterInfo = FlatPersonInquiryResponse.builder()
//                .firstName("تست")
//                .lastName("تستی")
//                .gender("مرد")
//                .birthDate(request.requesterBirthDate())
//                .nationalId(request.requesterNationalId())
//                .fatherName("تست")
//                .shenasnamehNumber("123")
//                .shenasnameSeri("123")
//                .deathStatus("در قید حیات")
//                .build();

        // Find deceased user by mobile or national ID
        User deceasedUser = userRepository.findByNationalIdOrMobileNumber(request.deceasedIdentifier(), request.deceasedIdentifier())
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND, "Deceased user not found."));

        // First, check if a case already exists in our system for this national ID
        Optional<InheritanceCase> existingCaseOpt = caseRepository.findByDeceasedUser_NaturalPersonProfile_NationalId(deceasedUser.getNaturalPersonProfile().getNationalId());

        if (existingCaseOpt.isPresent()) {
            InheritanceCase existingCase = existingCaseOpt.get();
            String deceasedName = deceasedUser.getNaturalPersonProfile().getFirstName() + " " + deceasedUser.getNaturalPersonProfile().getLastName();

            List<InheritanceDtos.HeirStatusDto> heirStatusDtos = existingCase.getHeirs().stream()
                    .map(heir -> InheritanceDtos.HeirStatusDto.fromEntity(heir, request.requesterNationalId()))
                    .collect(Collectors.toCollection(ArrayList::new));

            return new InheritanceDtos.DeathVerificationDetailResponse(
                    true,
                    "DECEASED_CASE_IN_PROGRESS",
                    existingCase.getTrackingCode(),
                    deceasedName,
                    existingCase.getStatus(),
                    heirStatusDtos,
                    existingCase.getCreatedAt(),
//                    requesterInfo.getFirstName() + " " + requesterInfo.getLastName(),
                    requesterInfo
            );
        }

        // If no case exists, proceed to the external inquiry
        // TODO remove this ...for now for develop is true
        FlatPersonInquiryResponse personInfo = personInquiryClient.getPersonInfo(
                request.deceasedIdentifier(),
                deceasedUser.getNaturalPersonProfile().getBirthDate()
        ).block();

        if (personInfo == null || personInfo.getDeathStatus() == null) {
            return new InheritanceDtos.DeathVerificationDetailResponse(false, "UNKNOWN", null, null, null, null, null, null);
        }

        boolean isDeceased = Objects.equals(personInfo.getDeathStatus(), "1");
        String deathStatus = isDeceased ? "DECEASED_NO_CASE" : "NOT_DECEASED";
        String deceasedName = isDeceased ? personInfo.getFirstName() + " " + personInfo.getLastName() : null;

        return new InheritanceDtos.DeathVerificationDetailResponse(isDeceased, deathStatus, null, deceasedName, null, null, null, null);
//        return new InheritanceDtos.DeathVerificationDetailResponse(true, "DECEASED_NO_CASE", null, "حسین حسینی", null, null, null, requesterInfo);

    }

    @Override
    public InheritanceDtos.CheckStatusResponse checkStatus(InheritanceDtos.CheckStatusRequest request) {
        otpService.verifyOtp(OtpPurpose.INHERITANCE_VERIFICATION, request.mobileNumber(), request.otpCode());

        Optional<Heir> heirOpt = heirRepository.findByNationalId(request.nationalId());

        if (heirOpt.isPresent()) {
            Heir heir = heirOpt.get();
            InheritanceCase inheritanceCase = heir.getInheritanceCase();
            User deceasedUser = inheritanceCase.getDeceasedUser();
            String deceasedName = deceasedUser.getNaturalPersonProfile().getFirstName() + " " + deceasedUser.getNaturalPersonProfile().getLastName();

            List<InheritanceDtos.HeirStatusDto> heirStatusDtos = inheritanceCase.getHeirs().stream()
                    .map(h -> InheritanceDtos.HeirStatusDto.fromEntity(h, heir.getNationalId()))
                    .collect(Collectors.toList());

            InheritanceDtos.CaseDataDto caseData = new InheritanceDtos.CaseDataDto(
                    inheritanceCase.getTrackingCode(),
                    deceasedName,
                    inheritanceCase.getStatus(),
                    heirStatusDtos
            );

            return new InheritanceDtos.CheckStatusResponse(InheritanceDtos.CheckStatusResponse.Status.EXISTING_CASE, caseData);
        } else {
            return new InheritanceDtos.CheckStatusResponse(InheritanceDtos.CheckStatusResponse.Status.NEW_USER, null);
        }
    }

    @Override
    public InheritanceDtos.InitiateResponse initiateCase(InheritanceDtos.InitiateRequest request) {
        // Find user by mobile or national ID
        User deceasedUser = userRepository.findByNationalIdOrMobileNumber(request.deceasedIdentifier(), request.deceasedIdentifier())
                .orElseThrow(() -> new ZarnabException(ExceptionType.USER_NOT_FOUND));

        // Check if a case already exists for this user
        if (caseRepository.existsByDeceasedUser_MobileNumber(deceasedUser.getMobileNumber())) {
            throw new ZarnabException(ExceptionType.INHERITANCE_CASE_ALREADY_EXISTS);
        }

        // Lock the deceased user's account
        deceasedUser.setAccountStatus(AccountStatus.LOCKED_INHERITANCE_PENDING);
        userRepository.save(deceasedUser);

        // Create the new inheritance case
        String trackingCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        InheritanceCase newCase = InheritanceCase.builder()
                .deceasedUser(deceasedUser)
                .trackingCode(trackingCode)
                .status(InheritanceStatus.INITIATED)
                .build();
        caseRepository.save(newCase);

        Heir heir = Heir.builder()
                .inheritanceCase(newCase)
                .documentStatus(HeirDocumentStatus.PENDING)
                .fullName(request.initiatorName())
                .nationalId(request.initiatorNationalId())
                .mobileNumber(request.initiatorMobile())
                .requester(true)
                .build();
        heirRepository.save(heir);

        // Send tracking code via SMS
        String smsMessage = translate("inheritance.initiate.success", trackingCode);
        smsService.sendSms(request.initiatorMobile(), smsMessage);

        return new InheritanceDtos.InitiateResponse(trackingCode, "Case initiated successfully. Tracking code sent via SMS.");
    }

    @Override
    @Transactional
    public void uploadInitiatorDocuments(String trackingCode, MultipartFile deathCertificate, MultipartFile inheritanceCertificate, MultipartFile initiatorNationalId) {
        InheritanceCase inheritanceCase = caseRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ZarnabException(ExceptionType.INHERITANCE_CASE_NOT_FOUND));

        if (inheritanceCase.getStatus() != InheritanceStatus.INITIATED) {
            throw new ZarnabException(ExceptionType.INVALID_CASE_STATUS);
        }

        Heir requesterHeir = heirRepository.findRequesterHeir(trackingCode).orElseThrow();
        requesterHeir.setNationalIdCardUrl(fileStorageService.uploadFile(initiatorNationalId).object());
        requesterHeir.setDocumentStatus(HeirDocumentStatus.UPLOADED);
        heirRepository.save(requesterHeir);

        inheritanceCase.setDeathCertificateUrl(fileStorageService.uploadFile(deathCertificate).object());
        inheritanceCase.setInheritanceCertificateUrl(fileStorageService.uploadFile(inheritanceCertificate).object());
        inheritanceCase.setStatus(InheritanceStatus.PENDING_ADMIN_REVIEW);

        caseRepository.save(inheritanceCase);
    }

    @Override
    public String verifyHeir(InheritanceDtos.HeirVerificationRequest request) {
        Heir heir = heirRepository.findByInheritanceCase_TrackingCodeAndNationalId(request.trackingCode(), request.nationalId())
                .orElseThrow(() -> new ZarnabException(ExceptionType.HEIR_NOT_FOUND));

        if (heir.getInheritanceCase().getStatus() != InheritanceStatus.PENDING_HEIR_DOCUMENTS) {
            throw new ZarnabException(ExceptionType.INVALID_CASE_STATUS, "Case is not awaiting heir documents.");
        }

        return "Heir verified successfully. You can now upload your national ID card.";
    }

    @Override
    public void uploadHeirDocument(String trackingCode, String nationalId, MultipartFile nationalIdCard) {
        Heir heir = heirRepository.findByInheritanceCase_TrackingCodeAndNationalId(trackingCode, nationalId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.HEIR_NOT_FOUND));

        // Allow re-upload if status is PENDING, UPLOADED or REJECTED
        var allowedStatusesForUpload = List.of(HeirDocumentStatus.PENDING, HeirDocumentStatus.UPLOADED, HeirDocumentStatus.REJECTED);
        if (!allowedStatusesForUpload.contains(heir.getDocumentStatus())) {
            throw new ZarnabException(ExceptionType.INVALID_CASE_STATUS, "Heir document cannot be uploaded in current status.");
        }

        heir.setNationalIdCardUrl(fileStorageService.uploadFile(nationalIdCard).object());
        heir.setDocumentStatus(HeirDocumentStatus.UPLOADED); // Set status to UPLOADED after successful upload
        heirRepository.save(heir);

        // Check if all heirs have uploaded their documents
        boolean allDocsUploaded = heir.getInheritanceCase().getHeirs().stream()
                .allMatch(h -> h.getDocumentStatus() == HeirDocumentStatus.UPLOADED || h.getDocumentStatus() == HeirDocumentStatus.APPROVED);

        if (allDocsUploaded) {
            heir.getInheritanceCase().setStatus(InheritanceStatus.PENDING_FINAL_APPROVAL);
            caseRepository.save(heir.getInheritanceCase());
        }
    }

    @Override
    public PageableResponse<InheritanceDtos.CaseResponse> listCases(PageableRequest pageableRequest) {
        Specification<InheritanceCase> spec = SpecificationBuilder.buildSpecification(pageableRequest);
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), pageableRequest.getSort());
        Page<InheritanceCase> casePage = caseRepository.findAll(spec, pageable);

        List<InheritanceDtos.CaseResponse> responses = casePage.getContent().stream()
                .map(InheritanceDtos.CaseResponse::fromEntity)
                .collect(Collectors.toList());

        return new PageableResponse<>(responses, casePage.getTotalElements(), casePage.getNumber(), casePage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public InheritanceDtos.CaseResponse getCase(Long caseId) {
        return caseRepository.findById(caseId)
                .map(InheritanceDtos.CaseResponse::fromEntity)
                .orElseThrow(() -> new ZarnabException(ExceptionType.INHERITANCE_CASE_NOT_FOUND));
    }

    @Override
    public void addHeirsToCase(Long caseId, InheritanceDtos.AddHeirsRequest request) {
        InheritanceCase inheritanceCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.INHERITANCE_CASE_NOT_FOUND));

        var validStates = List.of(InheritanceStatus.PENDING_HEIR_DOCUMENTS, InheritanceStatus.PENDING_ADMIN_REVIEW);
        if (!validStates.contains(inheritanceCase.getStatus())) {
            throw new ZarnabException(ExceptionType.INVALID_CASE_STATUS);
        }

        List<Heir> newHeirs = new ArrayList<>();
        for (InheritanceDtos.HeirDto heirDto : request.heirs()) {
            Heir heir = Heir.builder()
                    .inheritanceCase(inheritanceCase)
                    .fullName(heirDto.fullName())
                    .nationalId(heirDto.nationalId())
                    .documentStatus(HeirDocumentStatus.PENDING) // New heirs start as PENDING
                    .build();
            newHeirs.add(heir);
        }

        heirRepository.saveAll(newHeirs);
        inheritanceCase.setStatus(InheritanceStatus.PENDING_HEIR_DOCUMENTS);
        caseRepository.save(inheritanceCase);
    }

    @Override
    public void removeHeirFromCase(Long caseId, Long heirId) {
        Heir heir = heirRepository.findById(heirId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.HEIR_NOT_FOUND));

        if (!heir.getInheritanceCase().getId().equals(caseId)) {
            throw new ZarnabException(ExceptionType.HEIR_NOT_FOUND);
        }

        InheritanceCase inheritanceCase = heir.getInheritanceCase();
        var validStates = List.of(InheritanceStatus.PENDING_HEIR_DOCUMENTS, InheritanceStatus.PENDING_FINAL_APPROVAL);
        if (!validStates.contains(inheritanceCase.getStatus())) {
            throw new ZarnabException(ExceptionType.INVALID_CASE_STATUS);
        }

        heirRepository.delete(heir);
    }

    @Override
    public void updateCaseStatus(Long caseId, InheritanceDtos.UpdateCaseStatusRequest request) {
        InheritanceCase inheritanceCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.INHERITANCE_CASE_NOT_FOUND));

        inheritanceCase.setStatus(request.status());
        if (request.adminNotes() != null && !request.adminNotes().isBlank()) {
            inheritanceCase.setAdminNotes(request.adminNotes());
        }

        // If case is completed or rejected, unlock the user account (or handle as per business rules)
        if (request.status() == InheritanceStatus.COMPLETED || request.status() == InheritanceStatus.REJECTED) {
            User deceasedUser = inheritanceCase.getDeceasedUser();
            deceasedUser.setAccountStatus(AccountStatus.ACTIVE); // Or CLOSED, depending on rules
            userRepository.save(deceasedUser);
        }

        caseRepository.save(inheritanceCase);
    }

    @Override
    public void verifyHeirDocument(Long caseId, Long heirId, InheritanceDtos.UpdateHeirDocumentStatusRequest request) {
        Heir heir = heirRepository.findById(heirId)
                .orElseThrow(() -> new ZarnabException(ExceptionType.HEIR_NOT_FOUND));

        if (!heir.getInheritanceCase().getId().equals(caseId)) {
            throw new ZarnabException(ExceptionType.HEIR_NOT_FOUND, "Heir does not belong to this case.");
        }

        // Validate that the case is in a state where heir documents can be verified
        InheritanceCase inheritanceCase = heir.getInheritanceCase();
        var validCaseStatuses = List.of(InheritanceStatus.PENDING_HEIR_DOCUMENTS, InheritanceStatus.PENDING_FINAL_APPROVAL);
        if (!validCaseStatuses.contains(inheritanceCase.getStatus())) {
            throw new ZarnabException(ExceptionType.INVALID_CASE_STATUS, "Heir documents can only be verified when the case is pending heir documents or final approval.");
        }

        // Update heir document status and admin comment
        heir.setDocumentStatus(request.documentStatus());
        heir.setAdminDocumentComment(request.adminDocumentComment());
        heirRepository.save(heir);
    }

}
