package com.zarnab.panel.ingot.model;

import com.zarnab.panel.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "ingots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id")
	private User owner;
} 