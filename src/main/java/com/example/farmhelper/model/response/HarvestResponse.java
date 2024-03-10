package com.example.farmhelper.model.response;

import com.example.farmhelper.entity.Field;
import java.util.Set;
import lombok.Data;

@Data
public class HarvestResponse {

    private Long id;

    private String cropName;

    private Double price;

    private String cropId;

    private Double amount;

    private String monthAndYearOfCollection;

    private Set<Field> fields;
}
