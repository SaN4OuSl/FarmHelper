package com.example.farmhelper.service;

import com.example.farmhelper.entity.Field;
import com.example.farmhelper.entity.Harvest;
import java.util.Set;

public interface FieldHarvestService {

    void addFieldsToHarvest(Harvest harvest, Set<Long> fieldIds);

    Set<Field> getAllFieldsByHarvestId(Long harvestId);

}
