package com.zarnab.panel.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"path", "method"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String method;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "page_role_mapping", joinColumns = @JoinColumn(name = "page_role_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> roles;
}
