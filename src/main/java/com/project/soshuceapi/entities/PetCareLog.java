package com.project.soshuceapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pet_care_logs")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class PetCareLog {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "date", columnDefinition = "DATE", nullable = false)
    private String date;
    @Column(name = "note", columnDefinition = "TEXT", nullable = false)
    private String note;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    @Column(name = "updated_by", columnDefinition = "VARCHAR(36)")
    private String updatedBy;
    @JoinColumn(name = "created_by", columnDefinition = "VARCHAR(36)", nullable = false)
    private String createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
}
