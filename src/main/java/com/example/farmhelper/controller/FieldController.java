package com.example.farmhelper.controller;

import com.example.farmhelper.entity.Field;
import com.example.farmhelper.service.FieldService;
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
@RequestMapping("/fields")
@AllArgsConstructor
public class FieldController {

    private FieldService fieldService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Field createField(@Valid @RequestBody Field field) {
        return fieldService.createField(field);
    }


    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.CREATED)
    public Field updateField(@Valid @RequestBody Field field) {
        return fieldService.updateField(field);
    }

    @GetMapping("/{fieldId}")
    @ResponseStatus(HttpStatus.OK)
    public Field getFieldById(@PathVariable Long fieldId) {
        return fieldService.getFieldById(fieldId);
    }

    @DeleteMapping("/delete/{fieldId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteField(@PathVariable Long fieldId) {
        fieldService.deactivateFieldById(fieldId);
    }

    @GetMapping("/get-current-fields")
    @ResponseStatus(HttpStatus.OK)
    public List<Field> getAllCurrentFields(
        @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return fieldService.getListOfCurrentFields(search);
    }

    @GetMapping("/get-former-fields")
    @ResponseStatus(HttpStatus.OK)
    public List<Field> getAllFormerFields(
        @RequestParam(value = "search", required = false, defaultValue = "") String search) {
        return fieldService.getListOfFormerFields(search);
    }
}
