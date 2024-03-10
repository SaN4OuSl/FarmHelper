package com.example.farmhelper.mapper;

import com.example.farmhelper.entity.Crop;
import com.example.farmhelper.entity.Field;
import com.example.farmhelper.entity.FieldHarvest;
import com.example.farmhelper.entity.Harvest;
import com.example.farmhelper.model.request.HarvestRequest;
import com.example.farmhelper.model.response.CropHarvestResponse;
import com.example.farmhelper.model.response.HarvestResponse;
import com.example.farmhelper.service.CropService;
import com.example.farmhelper.service.FieldHarvestService;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = CropService.class)
public interface HarvestMapper {

    HarvestMapper INSTANCE = Mappers.getMapper(HarvestMapper.class);

    @Mapping(target = "transactions", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "monthYearOfCollection", source = "monthAndYearOfCollection")
    @Mapping(target = "crop", source = "cropId", qualifiedByName = "getCropById")
    Harvest fromHarvestRequest(HarvestRequest harvestRequest, @Context CropService cropService);

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "monthAndYearOfCollection", source = "monthYearOfCollection")
    @Mapping(target = "fieldSize", source = "harvest", qualifiedByName = "getFieldSize")
    @Mapping(target = "soilType", source = "harvest", qualifiedByName = "getSoilType")
    CropHarvestResponse toCropHarvestResponse(Harvest harvest);

    @Mapping(target = "cropName", source = "harvest", qualifiedByName = "getCropName")
    @Mapping(target = "monthAndYearOfCollection", source = "monthYearOfCollection")
    @Mapping(target = "cropId", source = "harvest", qualifiedByName = "getCropId")
    @Mapping(target = "fields", source = "id", qualifiedByName = "getFieldByHarvestId")
    HarvestResponse toHarvestResponse(Harvest harvest,
                                      @Context FieldHarvestService fieldHarvestService);

    @Named("getCropById")
    default Crop getCropById(Long id, @Context CropService cropService) {
        return cropService.getCropById(id);
    }

    @Named("getFieldByHarvestId")
    default Set<Field> getFieldByHarvestId(Long harvestId,
                                           @Context FieldHarvestService fieldHarvestService) {
        return fieldHarvestService.getAllFieldsByHarvestId(harvestId);
    }

    @Named("getCropName")
    default String getCropName(Harvest harvest) {
        return harvest.getCrop().getName();
    }

    @Named("getCropId")
    default Long getCropId(Harvest harvest) {
        return harvest.getCrop().getId();
    }


    @Named("getFieldSize")
    default Double getFieldSize(Harvest harvest) {
        return harvest.getFieldHarvests().stream()
            .map(FieldHarvest::getField)
            .mapToDouble(Field::getFieldSize)
            .sum();
    }

    @Named("getSoilType")
    default String getSoilType(Harvest harvest) {
        return harvest.getFieldHarvests().isEmpty() ? "" :
            harvest.getFieldHarvests().stream()
                .map(FieldHarvest::getField)
                .map(Field::getSoilType)
                .distinct()
                .collect(Collectors.joining(", "));
    }
}

