package com.zarnab.panel.inheritance.model;

import com.zarnab.panel.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "heirs")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Heir extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inheritance_case_id", nullable = false)
    private InheritanceCase inheritanceCase;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String nationalId;

//    @Column(nullable = false)
    private String mobileNumber;

    private String nationalIdCardUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HeirDocumentStatus documentStatus = HeirDocumentStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String adminDocumentComment;

    @Builder.Default
    private Boolean requester = false;
}
