package com.example.farmhelper.service.impl;

import com.example.farmhelper.entity.Field;
import com.example.farmhelper.entity.FieldHarvest;
import com.example.farmhelper.entity.Harvest;
import com.example.farmhelper.repository.FieldHarvestRepository;
import com.example.farmhelper.service.FieldHarvestService;
import com.example.farmhelper.service.FieldService;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FieldHarvestServiceImpl implements FieldHarvestService {

    private FieldHarvestRepository fieldHarvestRepository;

    private FieldService fieldService;

    @Override
    public void addFieldsToHarvest(Harvest harvest, Set<Long> fieldIds) {
        log.info("Method addFieldsToHarvest() started with fieldIds = {}", fieldIds);
        List<FieldHarvest> fieldHarvestList = fieldIds.stream()
            .map(fieldId -> {
                Field field = fieldService.getFieldById(fieldId);
                return FieldHarvest.builder()
                    .harvest(harvest)
                    .field(field)
                    .collectionDate(Timestamp.from(Instant.now()))
                    .build();
            })
            .collect(Collectors.toList());

        fieldHarvestRepository.saveAll(fieldHarvestList);
        log.info("Method addFieldsToHarvest() finished successfully");
    }

    @Override
    public Set<Field> getAllFieldsByHarvestId(Long harvestId) {
        log.info("Method getAllFieldsByHarvestId() started with harvestId = {}", harvestId);
        Set<Field> fields = fieldHarvestRepository.findAllByHarvestId(harvestId).stream()
            .map(FieldHarvest::getField)
            .collect(Collectors.toSet());
        log.info(
            "Method getAllFieldsByHarvestId() finished successfully, returned value: {}",
            fields.size());
        return fields;
    }
}
