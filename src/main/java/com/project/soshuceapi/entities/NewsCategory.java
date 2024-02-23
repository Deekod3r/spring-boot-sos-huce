package com.project.soshuceapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "news_categories")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class NewsCategory {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String name;
    @Column(name = "description", columnDefinition = "VARCHAR(255)")
    private String description;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private String createdAt;
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private String updatedAt;
    @Column(name = "created_by", columnDefinition = "VARCHAR(36)", nullable = false)
    private String createdBy;
    @Column(name = "updated_by", columnDefinition = "VARCHAR(36)")
    private String updatedBy;

    @JsonIgnore
    @OneToMany(mappedBy = "newsCategory", fetch = FetchType.LAZY)
    private Set<News> news = new HashSet<>();
}
