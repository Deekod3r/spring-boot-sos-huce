package com.project.soshuceapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "living_costs")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class LivingCost {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;
    @Column(name = "category", columnDefinition = "INTEGER", nullable = false)
    private Integer category; // 1: thuc an, 2: thuoc, 3: vat dung, 4: khac
    @Column(name = "cost", columnDefinition = "NUMERIC(38,2)", nullable = false)
    private BigDecimal cost;
    @Column(name = "date", columnDefinition = "DATE", nullable = false)
    private LocalDate date;
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN", nullable = false)
    private Boolean isDeleted;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime deletedAt;
    @Column(name = "updated_by", columnDefinition = "VARCHAR(36)")
    private String updatedBy;
    @Column(name = "deleted_by", columnDefinition = "VARCHAR(36)")
    private String deletedBy;
    @JoinColumn(name = "created_by", columnDefinition = "VARCHAR(36)", nullable = false)
    private String createdBy;

}
