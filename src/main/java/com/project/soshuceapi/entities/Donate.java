package com.project.soshuceapi.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "donates")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Donate {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "remitter", columnDefinition = "VARCHAR(100)", nullable = false)
    private String remitter;
    @Column(name = "payee", columnDefinition = "VARCHAR(100)", nullable = false)
    private String payee;
    @Column(name = "type", columnDefinition = "INT", nullable = false)
    private Integer type;
    @Column(name = "detail", columnDefinition = "VARCHAR(255)")
    private String detail;
    @Column(name = "amount", columnDefinition = "NUMERIC(38,2)", nullable = false)
    private BigDecimal amount;
    @Column(name = "date", columnDefinition = "DATE", nullable = false)
    private LocalDate date;

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
