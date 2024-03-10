package com.example.farmhelper.service.impl;

import com.example.farmhelper.entity.Field;
import com.example.farmhelper.exception.ResourceNotFoundException;
import com.example.farmhelper.repository.FieldRepository;
import com.example.farmhelper.service.FieldService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FieldServiceImpl implements FieldService {

    private FieldRepository fieldRepository;

    @Override
    public Field createField(Field field) {
        log.info("Method createField() started with {}", field);
        field.setIsActive(true);
        field = fieldRepository.save(field);
        log.info("Method createField() finished successfully, returned value: {}", field);
        return field;
    }

    @Override
    public Field updateField(Field updatedField) {
        log.info("Method updateField() started with {}", updatedField);
        Field field = getFieldById(updatedField.getId());
        field.setFieldName(updatedField.getFieldName());
        field.setFieldSize(updatedField.getFieldSize());
        field.setCoordinates(updatedField.getCoordinates());
        field.setSoilType(updatedField.getSoilType());
        field = fieldRepository.save(field);
        log.info("Method createField() finished successfully, returned value: {}", field);
        return field;
    }

    @Override
    public void deactivateFieldById(Long id) {
        log.info("Method deactivateFieldById() started with id = {}", id);
        Field field = getFieldById(id);
        field.setIsActive(false);
        fieldRepository.save(field);
        log.info("Method deactivateFieldById() finished successfully");
    }

    @Override
    public Field getFieldById(Long id) {
        log.info("Method getFieldById() started with id = {}", id);
        return fieldRepository.findById(id).orElseThrow(() -> {
            log.warn("Field with id = {} not found", id);
            return new ResourceNotFoundException(id.toString());
        });
    }

    @Override
    public Field getFieldByName(String fieldName) {
        log.info("Method getFieldByName() started with name = {}", fieldName);
        return fieldRepository.findByFieldName(fieldName).orElseThrow(() -> {
            log.warn("Field with name = {} not found", fieldName);
            return new ResourceNotFoundException(fieldName);
        });
    }

    @Override
    public List<Field> getListOfCurrentFields(String search) {
        log.info("Method getListOfCurrentFields() started with search = {}", search);
        List<Field> fields =
            fieldRepository.findAllByIsActiveAndFieldNameIsContainingIgnoreCase(true, search);
        log.info("Method getListOfCurrentFields() finished successfully with amountOfFields = {}",
            fields.size());
        return fields;
    }

    @Override
    public List<Field> getListOfFormerFields(String search) {
        log.info("Method getListOfFormerFields() started");
        return fieldRepository.findAllByIsActiveAndFieldNameIsContainingIgnoreCase(false, search);
    }
}
