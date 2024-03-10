package com.example.farmhelper.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fields", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"field_name"})
})
public class Field {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Column(name = "field_name", unique = true)
    @Getter
    @Setter
    private String fieldName;

    @Column(name = "field_size")
    @Getter
    @Setter
    private Double fieldSize;

    @Column(name = "coordinates", length = 4096)
    @Getter
    @Setter
    private String coordinates;

    @Column(name = "soil_type", nullable = false)
    @Getter
    @Setter
    private String soilType;

    @Column(name = "is_active", columnDefinition = "boolean default true", nullable = false)
    @Getter
    @Setter
    private Boolean isActive;

    @OneToMany(mappedBy = "field")
    @JsonIgnore
    private List<FieldHarvest> fieldHarvests = new ArrayList<>();
}
