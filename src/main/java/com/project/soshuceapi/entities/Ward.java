package com.project.soshuceapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wards")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Ward {
    @Id
    private int id;
    private String name;
    private String code;
    private int provinceId;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;
}
