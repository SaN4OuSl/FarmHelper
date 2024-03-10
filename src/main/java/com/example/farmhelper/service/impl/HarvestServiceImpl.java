package com.example.farmhelper.service.impl;

import com.example.farmhelper.entity.ActionType;
import com.example.farmhelper.entity.Crop;
import com.example.farmhelper.entity.Harvest;
import com.example.farmhelper.exception.ImpossibleAmount;
import com.example.farmhelper.exception.ResourceAlreadyExist;
import com.example.farmhelper.exception.ResourceNotFoundException;
import com.example.farmhelper.mapper.HarvestMapper;
import com.example.farmhelper.model.request.AddHarvestRequest;
import com.example.farmhelper.model.request.HarvestRequest;
import com.example.farmhelper.model.request.WriteOffHarvestRequest;
import com.example.farmhelper.model.response.CropHarvestResponse;
import com.example.farmhelper.model.response.HarvestResponse;
import com.example.farmhelper.repository.HarvestRepository;
import com.example.farmhelper.service.CropService;
import com.example.farmhelper.service.FieldHarvestService;
import com.example.farmhelper.service.HarvestService;
import com.example.farmhelper.service.TransactionService;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class HarvestServiceImpl implements HarvestService {

    private HarvestRepository harvestRepository;

    private CropService cropService;

    private FieldHarvestService fieldHarvestService;

    private TransactionService transactionService;

    @Override
    @Transactional
    public HarvestResponse createHarvest(HarvestRequest harvestRequest) {
        log.info("Method createHarvest() started with {}", harvestRequest);
        Harvest harvest = HarvestMapper.INSTANCE.fromHarvestRequest(harvestRequest, cropService);
        try {
            harvest = harvestRepository.save(harvest);
        } catch (RuntimeException e) {
            throw new ResourceAlreadyExist();
        }
        fieldHarvestService.addFieldsToHarvest(harvest, harvestRequest.getFieldIds());
        HarvestResponse harvestResponse =
            HarvestMapper.INSTANCE.toHarvestResponse(harvest, fieldHarvestService);
        transactionService.createTransaction(harvest, ActionType.ADD, harvest.getAmount(), null,
            null);
        log.info("Method createHarvest() finished successfully, returned value: {}",
            harvestResponse);
        return harvestResponse;
    }

    @Override
    public Harvest getHarvestById(Long id) {
        log.info("Method getHarvestById() started with id = {}", id);
        Harvest harvest = harvestRepository.findById(id).orElseThrow(() -> {
            log.warn("Harvest with id = {} not found", id);
            return new ResourceNotFoundException("Harvest", id.toString());
        });
        log.info("Method getHarvestById() finished successfully, returned value: {}", harvest);
        return harvest;
    }


    @Override
    public List<CropHarvestResponse> getAllHarvestsByCrop(Long cropId) {
        log.info("Method getAllHarvestsByCrop() started with cropId = {}", cropId);

        List<CropHarvestResponse> cropHarvestResponses;
        try {
            List<Harvest> harvests = harvestRepository.findAllByCropId(cropId);

            cropHarvestResponses = harvests.stream()
                .map(HarvestMapper.INSTANCE::toCropHarvestResponse)
                .collect(Collectors.toList());

            log.info("Method getAllHarvestsByCrop() finished successfully, returned value: {}",
                cropHarvestResponses.size());
        } catch (Exception e) {
            log.error("An error occurred while retrieving harvests for cropId = {}: {}", cropId,
                e.getMessage());
            throw new RuntimeException("Error retrieving harvests for cropId " + cropId, e);
        }

        log.info("Method getAllHarvestsByCrop() finished successfully, returned value: {}",
            cropHarvestResponses.size());
        return cropHarvestResponses;
    }

    @Override
    public List<HarvestResponse> getAllHarvests() {
        log.info("Method getAllHarvests() started");
        List<HarvestResponse> harvestResponses;
        try {
            List<Harvest> harvests = harvestRepository.findAll();

            harvestResponses = harvests.stream()
                .map(harvest -> HarvestMapper.INSTANCE.toHarvestResponse(harvest,
                    fieldHarvestService))
                .collect(Collectors.toList());

            log.info("Method getAllHarvests() finished successfully, returned value: {}",
                harvestResponses.size());
        } catch (Exception e) {
            log.error("An error occurred while retrieving harvests: {}",
                e.getMessage());
            throw new RuntimeException("Error retrieving harvests", e);
        }

        log.info("Method getAllHarvests() finished successfully, returned value: {}",
            harvestResponses.size());
        return harvestResponses;
    }

    @Override
    @Transactional
    public HarvestResponse addAmountToHarvest(AddHarvestRequest addHarvestRequest) {
        log.info("Method addAmountToHarvest() started with id = {} and amount = {}",
            addHarvestRequest.getId(), addHarvestRequest.getAmount());
        Harvest harvest = getHarvestById(addHarvestRequest.getId());
        harvest.setAmount(harvest.getAmount() + addHarvestRequest.getAmount());
        fieldHarvestService.addFieldsToHarvest(harvest, addHarvestRequest.getFieldIds());
        harvest = harvestRepository.save(harvest);
        transactionService.createTransaction(harvest, ActionType.ADD, addHarvestRequest.getAmount(),
            null, null);
        HarvestResponse harvestResponse =
            HarvestMapper.INSTANCE.toHarvestResponse(harvest, fieldHarvestService);
        log.info("Method addAmountToHarvest() finished successfully, returned value: {}",
            harvestResponse);
        return harvestResponse;
    }

    @Override
    @Transactional
    public HarvestResponse writeOffAmountFromHarvest(
        WriteOffHarvestRequest writeOffHarvestRequest) {
        log.info("Method writeOffAmountOfHarvest() started with id = {} and amount = {}",
            writeOffHarvestRequest.getId(),
            writeOffHarvestRequest.getAmount());
        Harvest harvest = getHarvestById(writeOffHarvestRequest.getId());
        validateAmount(harvest, writeOffHarvestRequest.getAmount());
        harvest.setAmount(harvest.getAmount() - writeOffHarvestRequest.getAmount());
        harvest = harvestRepository.save(harvest);
        transactionService.createTransaction(harvest, ActionType.WRITE_OFF,
            writeOffHarvestRequest.getAmount(),
            null, writeOffHarvestRequest.getExplanation());
        HarvestResponse harvestResponse =
            HarvestMapper.INSTANCE.toHarvestResponse(harvest, fieldHarvestService);
        log.info("Method writeOffAmountOfHarvest() finished successfully, returned value: {}",
            harvestResponse);
        return harvestResponse;
    }

    @Override
    public Harvest getHarvestByCropNameAndMonthAndYear(String cropName,
                                                       String monthAndYearOfCollection) {
        log.info("Method getHarvestByCropNameAndMonthAndYear() "
                + "started with cropName = {} and monthYearOfCollection = {}",
            cropName, monthAndYearOfCollection);
        Crop crop = cropService.getCropByName(cropName);
        Harvest harvest =
            harvestRepository.findByCropAndMonthYearOfCollection(crop, monthAndYearOfCollection)
                .orElseThrow(() -> {
                    log.warn("Harvest with crop = {} and monthYearOfCollection = {} not found",
                        crop.getName(), monthAndYearOfCollection);
                    return new ResourceNotFoundException("Harvest",
                        crop.getName() + monthAndYearOfCollection);
                });
        log.info(
            "Method getHarvestByCropNameAndMonthAndYear() finished successfully, returned value: {}",
            harvest);
        return harvest;
    }

    @Override
    @Transactional
    public void sellAmountFromHarvest(Harvest harvest, Double amount, Double unitPrice,
                                      String description) {
        log.info(
            "Method sellAmountFromHarvest() started with id = {}, amount = {} and unitPrice = {}",
            harvest.getId(), amount, unitPrice);
        validateAmount(harvest, amount);
        harvest.setAmount(harvest.getAmount() - amount);
        harvest = harvestRepository.save(harvest);
        transactionService.createTransaction(harvest, ActionType.SALE,
            amount,
            unitPrice, description);
        log.info("Method sellAmountFromHarvest() finished successfully");
    }

    @Override
    public void validateAmount(Harvest harvest, Double amount) {
        if (harvest.getAmount() - amount < 0) {
            throw new ImpossibleAmount();
        }
    }
}
