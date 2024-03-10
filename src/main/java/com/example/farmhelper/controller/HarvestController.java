package com.example.farmhelper.controller;

import com.example.farmhelper.model.request.AddHarvestRequest;
import com.example.farmhelper.model.request.HarvestRequest;
import com.example.farmhelper.model.request.WriteOffHarvestRequest;
import com.example.farmhelper.model.response.CropHarvestResponse;
import com.example.farmhelper.model.response.HarvestResponse;
import com.example.farmhelper.service.HarvestService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/harvests")
@AllArgsConstructor
public class HarvestController {

    private HarvestService harvestService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public HarvestResponse createHarvest(@Valid @RequestBody HarvestRequest harvestRequest) {
        return harvestService.createHarvest(harvestRequest);
    }

    @GetMapping("/{cropId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CropHarvestResponse> getAllHarvestOfCrop(@PathVariable Long cropId) {
        return harvestService.getAllHarvestsByCrop(cropId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<HarvestResponse> getAllHarvest() {
        return harvestService.getAllHarvests();
    }

    @PatchMapping("/add-amount")
    @ResponseStatus(HttpStatus.OK)
    public HarvestResponse addAmountToHarvest(
        @Valid @RequestBody AddHarvestRequest addHarvestRequest) {
        return harvestService.addAmountToHarvest(addHarvestRequest);
    }

    @PatchMapping("/write-off-amount")
    @ResponseStatus(HttpStatus.OK)
    public HarvestResponse writeOffAmountFromHarvest(
        @Valid @RequestBody WriteOffHarvestRequest writeOffHarvestRequest) {
        return harvestService.writeOffAmountFromHarvest(writeOffHarvestRequest);
    }
}
