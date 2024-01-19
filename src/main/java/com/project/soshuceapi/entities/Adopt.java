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
@Table(name = "adopts")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Adopt {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "ward_id", columnDefinition = "INTEGER", nullable = false)
    private int ward;
    @Column(name = "district_id", columnDefinition = "INTEGER", nullable = false)
    private int district;
    @Column(name = "province_id", columnDefinition = "INTEGER", nullable = false)
    private int province;
    @Column(name = "address", columnDefinition = "VARCHAR(100)", nullable = false)
    private String address;
    @Column(name = "status", columnDefinition = "INTEGER", nullable = false)
    private int status; // '0-wait for progressing; 1-in progress; 2-reject; 3-cancel; 4-complete'
    @Column(name = "is_deleted", columnDefinition = "BOOLEAN", nullable = false)
    private boolean isDeleted;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime deletedAt;
    @Column(name = "created_by", columnDefinition = "VARCHAR(36)", nullable = false)
    private String createdBy;
    @Column(name = "updated_by", columnDefinition = "VARCHAR(36)")
    private String updatedBy;
    @Column(name = "deleted_by", columnDefinition = "VARCHAR(36)")
    private String deletedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id")
    private Pet pet;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
