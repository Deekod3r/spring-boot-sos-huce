package com.project.soshuceapi.entities.logging;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "action_log_detail")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class ActionLogDetail {
    @Id
    private Long id;
    @Column(name = "action_log_id", columnDefinition = "BIGINT", nullable = false)
    private Long actionLogId;
    @Column(name = "table_name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String tableName;
    @Column(name = "column_name", columnDefinition = "VARCHAR(100)", nullable = false)
    private String columnName;
    @Column(name = "row_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private String rowId;
    @Column(name = "old_value", columnDefinition = "TEXT", nullable = false)
    private String oldValue;
    @Column(name = "new_value", columnDefinition = "TEXT", nullable = false)
    private String newValue;
}
