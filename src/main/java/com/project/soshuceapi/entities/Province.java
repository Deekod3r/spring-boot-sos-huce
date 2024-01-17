package com.project.soshuceapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @OneToMany(mappedBy = "province")
    @JsonIgnore
    private Set<District> districts = new HashSet<>();
}
