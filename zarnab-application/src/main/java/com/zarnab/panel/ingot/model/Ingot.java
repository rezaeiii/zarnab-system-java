package com.zarnab.panel.ingot.model;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "ingots")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Ingot extends BaseEntity {

	// سریال
	@Column(nullable = false, unique = true)
	private String serial;

	// تاریخ ساخت
	@Column(nullable = false)
	private LocalDate manufactureDate;

	// عیار
	@Column(nullable = false)
	private Integer karat;

	// گرم
	@Column(nullable = false)
	private Double weightGrams;

	// مالک
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "owner_id")
	private User owner;
} 