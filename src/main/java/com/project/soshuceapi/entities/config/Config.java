package com.project.soshuceapi.entities.config;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "configs")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Config {
    @Id
    private Long id;
    @Column(name = "key", columnDefinition = "VARCHAR(50)", nullable = false, unique = true)
    private String key;
    @Column(name = "description", columnDefinition = "VARCHAR(255)", nullable = false)
    private String description;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "updated_by", columnDefinition = "VARCHAR(36)")
    private String updatedBy;
}
