package com.example.farmhelper.service;

import com.example.farmhelper.entity.Field;
import java.util.List;

public interface FieldService {
    Field createField(Field field);

    Field updateField(Field updatedField);

    void deactivateFieldById(Long id);

    Field getFieldById(Long id);

    List<Field> getListOfCurrentFields(String search);

    List<Field> getListOfFormerFields(String search);

    Field getFieldByName(String fieldName);
}
