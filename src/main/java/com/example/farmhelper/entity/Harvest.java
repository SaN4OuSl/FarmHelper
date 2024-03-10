package com.example.farmhelper.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "harvests", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"crop_id", "month_and_year_of_collection"})
})
public class Harvest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "crop_id")
    @JsonIgnore
    @Getter
    @Setter
    private Crop crop;

    @Column(name = "amount")
    @Getter
    @Setter
    private Double amount;

    @Column(name = "month_and_year_of_collection", nullable = false)
    @Getter
    @Setter
    private String monthYearOfCollection;

    @OneToMany(mappedBy = "harvest")
    @Getter
    @JsonIgnore
    private List<FieldHarvest> fieldHarvests = new ArrayList<>();

    @OneToMany(mappedBy = "harvest")
    @Getter
    @Setter
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();
}
