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
@Table(name = "treatments")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Treatment {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;
    @Column(name = "start_date", columnDefinition = "DATE", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", columnDefinition = "DATE")
    private LocalDate endDate;
    @Column(name = "location", columnDefinition = "VARCHAR(255)", nullable = false)
    private String location;
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    @Column(name = "price", columnDefinition = "NUMERIC(38,2)", nullable = false)
    private BigDecimal price;
    @Column(name="quantity", columnDefinition = "INTEGER", nullable = false)
    private Integer quantity;
    @Column(name = "status", columnDefinition = "BOOLEAN", nullable = false)
    private Boolean status;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
}
