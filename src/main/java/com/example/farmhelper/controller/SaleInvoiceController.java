package com.example.farmhelper.controller;

import com.example.farmhelper.model.request.SaleInvoiceRequest;
import com.example.farmhelper.model.response.SaleInvoiceResponse;
import com.example.farmhelper.service.SaleInvoiceService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sale-invoices")
@AllArgsConstructor
public class SaleInvoiceController {

    private SaleInvoiceService saleInvoiceService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<SaleInvoiceResponse> getAllSaleInvoices() {
        return saleInvoiceService.getAllSaleInvoices();
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public SaleInvoiceResponse createSaleInvoice(
        @Valid @RequestBody SaleInvoiceRequest saleInvoiceRequest) {
        return saleInvoiceService.createSaleInvoice(saleInvoiceRequest);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SaleInvoiceResponse updateSaleInvoice(@PathVariable Long id, @Valid @RequestBody
    SaleInvoiceRequest saleInvoiceRequest) {
        return saleInvoiceService.updateSaleInvoice(id, saleInvoiceRequest);
    }

    @PatchMapping("/execute/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SaleInvoiceResponse executeSaleInvoice(@PathVariable Long id) {
        return saleInvoiceService.executeSaleInvoice(id);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCrop(@PathVariable Long id) {
        saleInvoiceService.deleteSaleInvoice(id);
    }
}
