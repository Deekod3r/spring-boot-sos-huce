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
@Table(name = "news")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class News {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "title", columnDefinition = "VARCHAR(255)", nullable = false)
    private String title;
    @Column(name = "description", columnDefinition = "VARCHAR(255)", nullable = false)
    private String description;
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    @Column(name = "image", columnDefinition = "TEXT", nullable = false)
    private String image;
    @Column(name = "status", columnDefinition = "BOOLEAN", nullable = false)
    private Boolean status;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    @Column(name = "updated_by", columnDefinition = "VARCHAR(36)")
    private String updatedBy;
    @JoinColumn(name = "created_by", columnDefinition = "VARCHAR(36)", nullable = false)
    private String createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "news_category_id", nullable = false)
    private NewsCategory newsCategory;
}
