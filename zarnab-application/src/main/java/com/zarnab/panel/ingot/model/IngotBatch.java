package com.zarnab.panel.ingot.model;

import com.zarnab.panel.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ingot_batches")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IngotBatch extends BaseEntity {

    @Column(nullable = false)
    private LocalDate manufactureDate;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL)
    private List<Ingot> ingots;
}
