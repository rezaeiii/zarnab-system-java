package com.zarnab.panel.clients.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonInquiryResponse {

    private BasicInformation basicInformation;
    private IdentificationInformation identificationInformation;
    private RegistrationStatus registrationStatus;
    private OfficeInformation officeInformation;
    private ResponseContext responseContext;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BasicInformation {
        private String firstName;
        private String lastName;
        private String fatherName;
        private String gender;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IdentificationInformation {
        private String nationalId;
        private String birthDate;
        private String shenasnameSeri;
        private String shenasnameSerial;
        private String shenasnamehNumber;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RegistrationStatus {
        // The type could be boolean, String, or an Enum depending on the API's actual values
        private String deathStatus;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OfficeInformation {
        private String officeCode;
        private String officeName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseContext {
        private Status status;
        private String requestId;
        private String correlationId;
        private String navigationURI;
        private String nextStepToken;
        private String userSessionId;
        private Map<String, Object> custom;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Status {
            private int code;
            private String message;
            private List<Object> details;
        }
    }
}