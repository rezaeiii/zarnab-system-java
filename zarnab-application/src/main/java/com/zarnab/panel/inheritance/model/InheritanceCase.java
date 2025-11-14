package com.zarnab.panel.inheritance.model;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "inheritance_cases")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InheritanceCase extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deceased_user_id", nullable = false)
    private User deceasedUser;

    @Column(nullable = false, unique = true)
    private String trackingCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InheritanceStatus status;

    // Information about the person who initiated the process
//    @Column(nullable = false)
//    private String initiatorName;
//
//    @Column(nullable = false)
//    private String initiatorMobile;

    // Documents uploaded by the initiator
    private String deathCertificateUrl;
    private String inheritanceCertificateUrl;
//    private String initiatorNationalIdUrl;

    @OneToMany(mappedBy = "inheritanceCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Heir> heirs;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @Transient
    public Heir getRequesterHeir() {
        if (heirs == null) {
            return null;
        }
        return heirs.stream().filter(Heir::getRequester).findFirst().orElse(null);
    }
}
