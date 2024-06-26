package com.project.soshuceapi.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "adopts")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
@Builder
public class Adopt {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "code", columnDefinition = "VARCHAR(15)", nullable = false)
    private String code;
    @Column(name = "ward_id", columnDefinition = "INTEGER", nullable = false)
    private Integer wardId;
    @Column(name = "district_id", columnDefinition = "INTEGER", nullable = false)
    private Integer districtId;
    @Column(name = "province_id", columnDefinition = "INTEGER", nullable = false)
    private Integer provinceId;
    @Column(name = "address", columnDefinition = "VARCHAR(255)", nullable = false)
    private String address;
    @Column(name = "status", columnDefinition = "INTEGER", nullable = false)
    private Integer status; // '1-wait for progressing; 2-in progress; 3-reject; 4-cancel; 5-complete'
    @Column(name = "reason", columnDefinition = "VARCHAR(255)")
    private String reason;
    @Column(name = "confirmed_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime confirmedAt;
    @Column(name = "rejected_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime rejectedAt;
    @Column(name = "rejected_reason", columnDefinition = "VARCHAR(255)")
    private String rejectedReason;
    @Column(name = "fee", columnDefinition = "NUMERIC(38,2)", nullable = false)
    private BigDecimal fee;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "registered_by", nullable = false)
    private User registeredBy;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rejected_by")
    private User rejectedBy;
}
