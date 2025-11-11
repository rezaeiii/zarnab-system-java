package com.zarnab.panel.clients.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlatPersonInquiryResponse {

    // BasicInformation
    private String firstName;
    private String lastName;
    private String fatherName;
    private String gender;

    // IdentificationInformation
    private String nationalId;
    private String birthDate;
    private String shenasnameSeri;
    private String shenasnameSerial;
    private String shenasnamehNumber;

    // RegistrationStatus
    private String deathStatus;

    // OfficeInformation
    private String officeCode;
    private String officeName;

    public static FlatPersonInquiryResponse from(PersonInquiryResponse response) {
        if (response == null) {
            return null;
        }

        return FlatPersonInquiryResponse.builder()
                .firstName(response.getBasicInformation() != null ? response.getBasicInformation().getFirstName() : null)
                .lastName(response.getBasicInformation() != null ? response.getBasicInformation().getLastName() : null)
                .fatherName(response.getBasicInformation() != null ? response.getBasicInformation().getFatherName() : null)
                .gender(response.getBasicInformation() != null ? response.getBasicInformation().getGender() : null)
                .nationalId(response.getIdentificationInformation() != null ? response.getIdentificationInformation().getNationalId() : null)
                .birthDate(response.getIdentificationInformation() != null ? response.getIdentificationInformation().getBirthDate() : null)
                .shenasnameSeri(response.getIdentificationInformation() != null ? response.getIdentificationInformation().getShenasnameSeri() : null)
                .shenasnameSerial(response.getIdentificationInformation() != null ? response.getIdentificationInformation().getShenasnameSerial() : null)
                .shenasnamehNumber(response.getIdentificationInformation() != null ? response.getIdentificationInformation().getShenasnamehNumber() : null)
                .deathStatus(response.getRegistrationStatus() != null ? response.getRegistrationStatus().getDeathStatus() : null)
                .officeCode(response.getOfficeInformation() != null ? response.getOfficeInformation().getOfficeCode() : null)
                .officeName(response.getOfficeInformation() != null ? response.getOfficeInformation().getOfficeName() : null)
                .build();
    }
}
