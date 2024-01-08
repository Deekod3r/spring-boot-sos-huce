package com.project.soshuceapi.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "districts")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class District {
    @Id
    private int id;
    private String name;
    private String code;

    @OneToMany(mappedBy = "district")
    private Set<Ward> wards = new HashSet<>();
}
