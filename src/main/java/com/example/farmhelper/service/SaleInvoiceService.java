package com.example.farmhelper.service;

import com.example.farmhelper.entity.SaleInvoice;
import com.example.farmhelper.model.request.SaleInvoiceRequest;
import com.example.farmhelper.model.response.SaleInvoiceResponse;
import java.util.List;

public interface SaleInvoiceService {

    SaleInvoiceResponse createSaleInvoice(SaleInvoiceRequest saleInvoiceRequest);

    SaleInvoiceResponse updateSaleInvoice(Long id, SaleInvoiceRequest updateSaleInvoiceRequest);

    List<SaleInvoiceResponse> getAllSaleInvoices();

    SaleInvoiceResponse executeSaleInvoice(Long id);

    void deleteSaleInvoice(Long id);

    SaleInvoice getSaleInvoiceById(Long id);
}
