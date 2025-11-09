package com.zarnab.panel.ingot.model;

import com.zarnab.panel.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

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

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ingot> ingots;

    @Formula("(select count(i.id) from ingots i where i.batch_id = id)")
    private int ingotCount;

    @Formula("(SELECT array_to_string((array_agg(i.serial ORDER BY i.serial DESC))[1:5], ', ') FROM ingots i WHERE i.batch_id = id)")
    private String lastFiveSerials;
}
