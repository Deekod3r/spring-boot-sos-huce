package com.project.soshuceapi.entities.locations;

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
    private Integer id;
    private String name;
    private String code;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;
}