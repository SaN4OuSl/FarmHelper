package com.example.farmhelper.service.impl;

import com.example.farmhelper.entity.Crop;
import com.example.farmhelper.exception.ResourceAlreadyExist;
import com.example.farmhelper.exception.ResourceNotFoundException;
import com.example.farmhelper.repository.CropRepository;
import com.example.farmhelper.repository.HarvestRepository;
import com.example.farmhelper.service.CropService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CropServiceImpl implements CropService {

    private CropRepository cropRepository;

    private HarvestRepository harvestRepository;

    @Override
    public Crop createCrop(Crop crop) {
        log.info("Method createCrop() started with {}", crop);
        crop = cropRepository.save(crop);
        log.info("Method createCrop() finished successfully, returned value: {}", crop);
        return crop;
    }

    @Override
    public void deleteCropById(Long id) {
        log.info("Method deleteCropById() started with id = {}", id);
        validateForHarvestsForCrop(id);
        cropRepository.deleteCropById(id);
        log.info("Method deleteCropById() finished successfully");
    }

    @Override
    public Crop updateCropById(Long id, Crop updatedCrop) {
        log.info("Method updateCropById() started with id = {} and updatedCropRequest = {}", id,
            updatedCrop);
        Crop crop = getCropById(id);
        crop.setDescription(updatedCrop.getDescription());
        crop.setName(updatedCrop.getName());
        log.info("Method updateCropById() finished successfully, returned value: {}", crop);
        return cropRepository.save(crop);
    }

    @Override
    public Crop getCropById(Long id) {
        log.info("Method getCropById() started with id = {}", id);
        Crop crop = cropRepository.findById(id).orElseThrow(() -> {
            log.warn("Crop with id = {} not found", id);
            return new ResourceNotFoundException("Crop", id.toString());
        });
        log.info("Method getCropById() finished successfully, returned value: {}", crop);
        return crop;
    }

    @Override
    public Crop getCropByName(String name) {
        log.info("Method getCropByName() started with name = {}", name);
        Crop crop = cropRepository.findByName(name.trim()).orElseThrow(() -> {
            log.warn("Crop with name = {} not found", name);
            return new ResourceNotFoundException("Crop", name);
        });
        log.info("Method getCropByName() finished successfully, returned value: {}", crop);
        return crop;
    }

    @Override
    public List<Crop> getAllCrops(String search) {
        log.info("Method getAllCrops() started with and search = {}", search);
        List<Crop> crops = cropRepository.findAllByNameContainingIgnoreCase(search);
        log.info(
            "Method getAllCrops() finished successfully, returned value: {}",
            crops.size());
        return crops;
    }

    private void validateForHarvestsForCrop(Long cropId) {
        boolean hasTransaction = harvestRepository.existsByCrop_Id(cropId);
        if (hasTransaction) {
            throw new ResourceAlreadyExist("Harvests", "crop");
        }
    }
}
