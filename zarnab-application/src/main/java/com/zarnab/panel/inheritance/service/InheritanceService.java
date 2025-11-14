package com.zarnab.panel.inheritance.service;

import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.inheritance.dto.InheritanceDtos;
import org.springframework.web.multipart.MultipartFile;

public interface InheritanceService {

    // == PUBLIC METHODS == //

    /**
     * Verifies the death status of a person and returns case details if a case exists.
     * @param request The request containing the deceased's details and the inquirer's ID.
     * @return A detailed response about the death status and any existing case.
     */
    InheritanceDtos.DeathVerificationDetailResponse verifyDeathStatus(InheritanceDtos.DeathVerificationRequest request);

    /**
     * Checks the status of a user's involvement in an inheritance case.
     * @param request The request containing the user's mobile number and OTP.
     * @return A response indicating if a case exists or if the user is new.
     */
    InheritanceDtos.CheckStatusResponse checkStatus(InheritanceDtos.CheckStatusRequest request);

    /**
     * Initiates a new inheritance case.
     * @param request DTO containing deceased's identifier and initiator's info.
     * @return A response with the tracking code.
     */
    InheritanceDtos.InitiateResponse initiateCase(InheritanceDtos.InitiateRequest request);

    /**
     * Uploads initial documents for the initiator.
     * @param trackingCode The case tracking code.
     * @param deathCertificate The death certificate file.
     * @param inheritanceCertificate The inheritance certificate file.
     * @param initiatorNationalId The initiator's national ID card file.
     */
    void uploadInitiatorDocuments(String trackingCode, MultipartFile deathCertificate, MultipartFile inheritanceCertificate, MultipartFile initiatorNationalId);

    /**
     * Verifies an heir against a case using their national ID.
     * @param request DTO containing tracking code and heir's national ID.
     * @return A simple confirmation or a token for document upload.
     */
    String verifyHeir(InheritanceDtos.HeirVerificationRequest request);

    /**
     * Allows a verified heir to upload their identity document.
     * @param trackingCode The case tracking code.
     * @param nationalId The heir's national ID.
     * @param nationalIdCard The heir's national ID card file.
     */
    void uploadHeirDocument(String trackingCode, String nationalId, MultipartFile nationalIdCard);


    // == ADMIN METHODS == //

    /**
     * Lists all inheritance cases with pagination and filtering.
     * @param pageableRequest The pageable request.
     * @return A paginated list of cases.
     */
    PageableResponse<InheritanceDtos.CaseResponse> listCases(PageableRequest pageableRequest);

    /**
     * Gets a single inheritance case by its ID.
     * @param caseId The ID of the case.
     * @return The detailed case response.
     */
    InheritanceDtos.CaseResponse getCase(Long caseId);

    /**
     * Adds heirs to a case after admin review.
     * @param caseId The ID of the case.
     * @param request DTO containing the list of heirs.
     */
    void addHeirsToCase(Long caseId, InheritanceDtos.AddHeirsRequest request);

    /**
     * Removes an heir from a case.
     * @param caseId The ID of the case.
     * @param heirId The ID of the heir to be removed.
     */
    void removeHeirFromCase(Long caseId, Long heirId);

    /**
     * Updates the status of a case (e.g., approve, reject).
     * @param caseId The ID of the case.
     * @param request DTO containing the new status and any admin notes.
     */
    void updateCaseStatus(Long caseId, InheritanceDtos.UpdateCaseStatusRequest request);

    /**
     * Manually verifies an heir's uploaded documents.
     * @param caseId The ID of the case.
     * @param heirId The ID of the heir.
     * @param request DTO containing the new document status and admin notes.
     */
    void verifyHeirDocument(Long caseId, Long heirId, InheritanceDtos.UpdateHeirDocumentStatusRequest request);
}
