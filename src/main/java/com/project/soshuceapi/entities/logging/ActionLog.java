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
    @GeneratedValue(strategy= GenerationType.AUTO)
    @SequenceGenerator(name = "action_log_seq", sequenceName = "action_log_seq", allocationSize = 1)
    private Long id;
    @Column(name = "action")
    private String action;
    @Column(name = "description")
    private String description;
    @Column(name = "ip")
    private String ip;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
