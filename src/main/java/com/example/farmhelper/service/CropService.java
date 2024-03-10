package com.example.farmhelper.service;

import com.example.farmhelper.entity.Crop;
import java.util.List;

public interface CropService {

    Crop createCrop(Crop crop);

    void deleteCropById(Long id);

    Crop updateCropById(Long id, Crop updatedCrop);

    Crop getCropById(Long id);

    Crop getCropByName(String name);


    List<Crop> getAllCrops(String search);
}
