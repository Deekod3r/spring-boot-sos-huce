package com.project.soshuceapi.entities.logging;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "action_log")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ActionLog {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name = "action", columnDefinition = "VARCHAR(100)", nullable = false)
    private String action;
    @Column(name = "description", columnDefinition = "VARCHAR(100)", nullable = false)
    private String description;
    @Column(name = "ip", columnDefinition = "VARCHAR(100)", nullable = false)
    private String ip;
    @Column(name = "created_by", columnDefinition = "VARCHAR(36)", nullable = false)
    private String createdBy;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;
}
