package com.example.farmhelper.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "field_harvest")
public class FieldHarvest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @ToString.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "harvest_id")
    @JsonIgnore
    @Getter
    @Setter
    private Harvest harvest;

    @Column(name = "harvest_id", insertable = false, updatable = false)
    @Getter
    @ToString.Include
    private Long harvestId;

    @ManyToOne
    @JoinColumn(name = "field_id")
    @JsonIgnore
    @Getter
    @Setter
    private Field field;

    @Column(name = "field_id", insertable = false, updatable = false)
    @Getter
    @ToString.Include
    private Long fieldId;

    @Column(name = "collection_date", nullable = false)
    @Getter
    @Setter
    private Timestamp collectionDate;
}
