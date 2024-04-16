package com.project.soshuceapi.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "feedbacks")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Feedback {
    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;
    @Column(name = "full_name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String fullName;
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;
    @Column(name = "email", columnDefinition = "VARCHAR(100)", nullable = false)
    private String email;
    @Column(name = "is_read", columnDefinition = "BOOLEAN", nullable = false)
    private Boolean isRead;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
}
