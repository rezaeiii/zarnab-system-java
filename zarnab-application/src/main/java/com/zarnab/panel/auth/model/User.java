package com.zarnab.panel.auth.model;

import com.zarnab.panel.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private boolean enabled;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> roles;

    // Common fields
    private String address;
    private String postalCode;
    private String city;
    private String province;

    // Profile type discriminator
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserProfileType profileType;

    // Embedded profiles - only one will be populated per user
    @Embedded
    private NaturalPersonProfileEmbeddable naturalPersonProfile;

    @Embedded
    private LegalPersonProfileEmbeddable legalPersonProfile;

}

