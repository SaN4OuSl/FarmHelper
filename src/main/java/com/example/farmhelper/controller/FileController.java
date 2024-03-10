package com.example.farmhelper.controller;

import com.example.farmhelper.entity.ActionType;
import com.example.farmhelper.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@AllArgsConstructor
public class FileController {

    private FileService fileService;

    @GetMapping("/transactions")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> exportTransactions(
        @RequestParam(value = "startDate", required = false)
        Long startDate, @RequestParam(value = "endDate", required = false)
        Long endDate, @RequestParam(value = "actionType", required = false)
        ActionType actionType) {
        byte[] file = fileService.exportTransactions(startDate, endDate, actionType);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"transactions.xlsx\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file);
    }

    @GetMapping("/crops")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> exportCrops(@RequestParam(value = "year") String year) {
        byte[] file = fileService.exportCrops(year);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"crops - " + year + ".xlsx\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file);
    }

    @PostMapping("/sale-invoices")
    @ResponseStatus(HttpStatus.OK)
    public void importSaleInvoices(@RequestPart("file") MultipartFile file) {
        fileService.importSellInvoices(file);
    }

    @PostMapping("/harvests")
    @ResponseStatus(HttpStatus.OK)
    public void importHarvests(@RequestPart("file") MultipartFile file) {
        fileService.importHarvestList(file);
    }

    @GetMapping("/harvests/example")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> exampleHarvests() {
        byte[] file = fileService.exportExampleHarvests();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"import-harvests-example.xlsx\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file);
    }

    @GetMapping("/sale-invoices/example")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> exampleSaleInvoices() {
        byte[] file = fileService.exportExampleSaleInvoices();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"import-sale-invoices-example.xlsx\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file);
    }
}
