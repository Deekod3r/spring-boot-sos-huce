package com.project.soshuceapi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@Table(name = "images")
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class Image {
    @Id
    private int id;
    private String name;
    private String url;
}
