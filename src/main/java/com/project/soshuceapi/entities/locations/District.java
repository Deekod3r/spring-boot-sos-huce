package com.project.soshuceapi.entities.locations;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Integer id;
    private String name;
    private String code;

    @JsonIgnore
    @OneToMany(mappedBy = "district", fetch = FetchType.LAZY)
    private Set<Ward> wards = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;
}
