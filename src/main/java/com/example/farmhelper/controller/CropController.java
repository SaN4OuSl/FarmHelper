package com.example.farmhelper.controller;

import com.example.farmhelper.entity.Crop;
import com.example.farmhelper.service.CropService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crops")
@AllArgsConstructor
public class CropController {

    private CropService cropService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Crop createCrop(@Valid @RequestBody Crop crop) {
        return cropService.createCrop(crop);
    }


    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCrop(@PathVariable Long id) {
        cropService.deleteCropById(id);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Crop updateCrop(@PathVariable Long id, @Valid @RequestBody Crop crop) {
        return cropService.updateCropById(id, crop);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Crop> getAllCrops(
        @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return cropService.getAllCrops(search);
    }
}
