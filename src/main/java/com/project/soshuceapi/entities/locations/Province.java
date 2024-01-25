package com.project.soshuceapi.entities.locations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.soshuceapi.entities.locations.District;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "provinces")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Province {
    @Id
    private int id;
    private String name;
    private String code;

    @OneToMany(mappedBy = "province", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<District> districts = new HashSet<>();
}
