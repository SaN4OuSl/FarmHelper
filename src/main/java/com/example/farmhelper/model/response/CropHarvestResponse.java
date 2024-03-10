package com.example.farmhelper.model.response;

import lombok.Data;

@Data
public class CropHarvestResponse {

    private Long id;

    private Double amount;

    private String monthAndYearOfCollection;

    private Double fieldSize;

    private String soilType;
}
