package com.example.farmhelper.service;

import com.example.farmhelper.entity.Harvest;
import com.example.farmhelper.model.request.AddHarvestRequest;
import com.example.farmhelper.model.request.HarvestRequest;
import com.example.farmhelper.model.request.WriteOffHarvestRequest;
import com.example.farmhelper.model.response.CropHarvestResponse;
import com.example.farmhelper.model.response.HarvestResponse;
import java.util.List;

public interface HarvestService {

    HarvestResponse createHarvest(HarvestRequest harvestRequest);

    Harvest getHarvestById(Long id);

    List<CropHarvestResponse> getAllHarvestsByCrop(Long cropId);

    List<HarvestResponse> getAllHarvests();

    HarvestResponse addAmountToHarvest(AddHarvestRequest addHarvestRequest);

    HarvestResponse writeOffAmountFromHarvest(WriteOffHarvestRequest writeOffHarvestRequest);

    Harvest getHarvestByCropNameAndMonthAndYear(String cropName,
                                                String monthAndYearOfCollection);

    void sellAmountFromHarvest(Harvest harvest, Double amount, Double unitPrice,
                               String description);

    void validateAmount(Harvest harvest, Double amount);
}
