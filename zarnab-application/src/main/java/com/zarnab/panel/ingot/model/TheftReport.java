package com.zarnab.panel.ingot.model;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TheftReport extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "ingot_id", nullable = false)
    private Ingot ingot;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TheftReportType type;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TheftReportStatus status;
}
