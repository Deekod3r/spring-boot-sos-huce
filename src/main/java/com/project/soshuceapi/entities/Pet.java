package com.project.soshuceapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pets")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Pet {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "code", columnDefinition = "VARCHAR(15)", nullable = false)
    private String code;
    @Column(name = "name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;
    @Column(name = "type", columnDefinition = "INTEGER", nullable = false)
    private int type;
    @Column(name = "breed", columnDefinition = "VARCHAR(100)", nullable = false)
    private String breed;
    @Column(name = "color", columnDefinition = "VARCHAR(50)", nullable = false)
    private String color;
    @Column(name = "age", columnDefinition = "VARCHAR(50)", nullable = false)
    private String age;
    @Column(name = "gender", columnDefinition = "INTEGER", nullable = false)
    private int gender;
    @Column(name = "image", columnDefinition = "VARCHAR(100)", nullable = false)
    private String image;
    @Column(name = "status", columnDefinition = "INTEGER", nullable = false)
    private int status;
    @Column(name = "weight", columnDefinition = "FLOAT", nullable = false)
    private float weight;
    @Column(name = "vaccin", columnDefinition = "INTEGER", nullable = false)
    private int vaccin;
    @Column(name = "sterilization", columnDefinition = "INTEGER", nullable = false)
    private int sterilization;
    @Column(name = "diet", columnDefinition = "INTEGER", nullable = false)
    private int diet;
    @Column(name = "rabies", columnDefinition = "INTEGER", nullable = false)
    private int rabies;
    @Column(name = "toilet", columnDefinition = "INTEGER", nullable = false)
    private int toilet;
    @Column(name = "friendlyToHuman", columnDefinition = "INTEGER", nullable = false)
    private int friendlyToHuman;
    @Column(name = "friendlyToDogs", columnDefinition = "INTEGER", nullable = false)
    private int friendlyToDogs;
    @Column(name = "friendlyToCats", columnDefinition = "INTEGER", nullable = false)
    private int friendlyToCats;
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
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

    @OneToMany(mappedBy = "pet")
    private Set<Adopt> adopts = new HashSet<>();
    @OneToMany(mappedBy = "pet")
    private Set<Treatment> treatments = new HashSet<>();
}
