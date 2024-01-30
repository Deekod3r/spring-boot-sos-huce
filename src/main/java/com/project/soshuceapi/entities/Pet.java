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
    @Column(name = "name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;
    @Column(name = "type", columnDefinition = "INTEGER", nullable = false)
    private int type; // '0-dog; 1-cat; 2-other'
    @Column(name = "breed", columnDefinition = "VARCHAR(100)", nullable = false)
    private String breed;
    @Column(name = "color", columnDefinition = "VARCHAR(50)", nullable = false)
    private String color;
    @Column(name = "age", columnDefinition = "INTEGER", nullable = false)
    private int age; // '0-young; 1-mature; 2-old; unknown'
    @Column(name = "gender", columnDefinition = "INTEGER", nullable = false)
    private int gender; // '0-male; 1-female; 2-unknown'
    @Column(name = "image", columnDefinition = "VARCHAR(1000)", nullable = false)
    private String image;
    @Column(name = "status", columnDefinition = "INTEGER", nullable = false)
    private int status; // '0-died; 1-adopted; 2-healing; 3-wait for adopting'
    @Column(name = "weight", columnDefinition = "FLOAT", nullable = false)
    private float weight;
    @Column(name = "vaccin", columnDefinition = "INTEGER", nullable = false)
    private int vaccin; //benh tong hp '0-no; 1-yes; 2-unknown'
    @Column(name = "sterilization", columnDefinition = "INTEGER", nullable = false)
    private int sterilization; //triet san '0-no; 1-yes; 2-unknown'
    @Column(name = "diet", columnDefinition = "INTEGER", nullable = false)
    private int diet; //che do an dac biet '0-no; 1-yes; 2-unknown'
    @Column(name = "rabies", columnDefinition = "INTEGER", nullable = false)
    private int rabies; //tiem dai '0-no; 1-yes; 2-unknown'
    @Column(name = "toilet", columnDefinition = "INTEGER", nullable = false)
    private int toilet; //biet di ve sinh '0-no; 1-yes; 2-unknown'
    @Column(name = "friendlyToHuman", columnDefinition = "INTEGER", nullable = false)
    private int friendlyToHuman; // '0-no; 1-yes; 2-unknown'
    @Column(name = "friendlyToDogs", columnDefinition = "INTEGER", nullable = false)
    private int friendlyToDogs; // '0-no; 1-yes; 2-unknown'
    @Column(name = "friendlyToCats", columnDefinition = "INTEGER", nullable = false)
    private int friendlyToCats; // '0-no; 1-yes; 2-unknown'
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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

    @OneToMany(mappedBy = "pet", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Adopt> adopts = new HashSet<>();
    @OneToMany(mappedBy = "pet", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Treatment> treatments = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "adopted_by")
    private User adoptedBy;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
}
