package com.project.soshuceapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

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
    @Column(name = "end_date", columnDefinition = "DATE", nullable = false)
    private LocalDate endDate;
    @Column(name = "location", columnDefinition = "VARCHAR(255)", nullable = false)
    private String location;
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    @Column(name = "price", columnDefinition = "BIGINT", nullable = false)
    private long price;
    @Column(name = "status", columnDefinition = "BOOLEAN", nullable = false)
    private boolean status;
    @Column(name = "bill", columnDefinition = "VARCHAR(1000)")
    private String bill;

    @Column(name = "is_deleted", columnDefinition = "BOOLEAN", nullable = false)
    private boolean isDeleted;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
