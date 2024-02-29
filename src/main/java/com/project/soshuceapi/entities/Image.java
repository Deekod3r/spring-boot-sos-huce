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
@Table(name = "images")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Image {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "file_name", columnDefinition = "TEXT", nullable = false)
    private String fileName;
    @Column(name = "file_type", columnDefinition = "VARCHAR(100)", nullable = false)
    private String fileType;
    @Column(name = "file_url", columnDefinition = "TEXT", nullable = false)
    private String fileUrl;
    @Column(name = "object_name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String objectName;
    @Column(name = "object_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String objectId;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
    @JoinColumn(name = "created_by", columnDefinition = "VARCHAR(36)", nullable = false)
    private String createdBy;
}
