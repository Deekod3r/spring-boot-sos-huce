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
@Table(name = "config_values")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ConfigValue {
    @Id
    private Long id;
    @Column(name = "config_id", columnDefinition = "INTEGER", nullable = false)
    private Integer configId;
    @Column(name = "key_cv", columnDefinition = "VARCHAR(50)", nullable = false, unique = true)
    private String keyCV;
    @Column(name = "value", columnDefinition = "TEXT", nullable = false)
    private String value;
    @Column(name = "type", columnDefinition = "INTEGER", nullable = false)
    private Integer type; //1: text, 2: html
    @Column(name = "description", columnDefinition = "VARCHAR(255)", nullable = false)
    private String description;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "updated_by", columnDefinition = "VARCHAR(36)")
    private String updatedBy;
}
