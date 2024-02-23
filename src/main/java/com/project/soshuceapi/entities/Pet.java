package com.project.soshuceapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
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
@SQLRestriction("is_deleted = false")
public class Pet {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "code", columnDefinition = "VARCHAR(15)", nullable = false)
    private String code;
    @Column(name = "name", columnDefinition = "VARCHAR(50)", nullable = false)
    private String name;
    @Column(name = "type", columnDefinition = "INTEGER", nullable = false)
    private Integer type; // '1-dog; 2-cat; 3-other'
    @Column(name = "breed", columnDefinition = "VARCHAR(100)", nullable = false)
    private String breed;
    @Column(name = "color", columnDefinition = "VARCHAR(100)", nullable = false)
    private String color;
    @Column(name = "age", columnDefinition = "INTEGER", nullable = false)
    private Integer age; // '1-young; 2-mature; 3-old; 4-unknown'
    @Column(name = "gender", columnDefinition = "INTEGER", nullable = false)
    private Integer gender; // '1-male; 2-female; 3-unknown'
    @Column(name = "image", columnDefinition = "TEXT", nullable = false)
    private String image;
    @Column(name = "status", columnDefinition = "INTEGER", nullable = false)
    private Integer status; // '1-died; 2-adopted; 3-healing; 4-wait for adopting'
    @Column(name = "weight", columnDefinition = "FLOAT", nullable = false)
    private Float weight;
    @Column(name = "vaccin", columnDefinition = "INTEGER", nullable = false)
    private Integer vaccin; //benh tong hp '1-no; 2-yes; 3-unknown'
    @Column(name = "sterilization", columnDefinition = "INTEGER", nullable = false)
    private Integer sterilization; //triet san '1-no; 2-yes; 3-unknown'
    @Column(name = "diet", columnDefinition = "INTEGER", nullable = false)
    private Integer diet; //che do an dac biet '1-no; 2-yes; 3-unknown'
    @Column(name = "rabies", columnDefinition = "INTEGER", nullable = false)
    private Integer rabies; //tiem dai '1-no; 2-yes; 3-unknown'
    @Column(name = "toilet", columnDefinition = "INTEGER", nullable = false)
    private Integer toilet; //biet di ve sinh '1-no; 2-yes; 3-unknown'
    @Column(name = "friendlyToHuman", columnDefinition = "INTEGER", nullable = false)
    private Integer friendlyToHuman; // '1-no; 2-yes; 3-unknown'
    @Column(name = "friendlyToDogs", columnDefinition = "INTEGER", nullable = false)
    private Integer friendlyToDogs; // '1-no; 2-yes; 3-unknown'
    @Column(name = "friendlyToCats", columnDefinition = "INTEGER", nullable = false)
    private Integer friendlyToCats; // '1-no; 2-yes; 3-unknown'
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

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
    @Column(name = "created_by", columnDefinition = "VARCHAR(36)", nullable = false)
    private String createdBy;

    @JsonIgnore
    @OneToMany(mappedBy = "pet", fetch = FetchType.LAZY)
    private Set<Adopt> adopts = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "pet", fetch = FetchType.LAZY)
    private Set<Treatment> treatments = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "adopted_by")
    private User adoptedBy;
}
