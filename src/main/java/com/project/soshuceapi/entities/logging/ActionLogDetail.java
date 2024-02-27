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
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(name = "action_log_id")
    private Long actionLogId;
    @Column(name = "table_name")
    private String tableName;
    @Column(name = "column_name")
    private String columnName;
    @Column(name = "row_id")
    private String rowId;
    @Column(name = "old_value")
    private String oldValue;
    @Column(name = "new_value")
    private String newValue;
}
