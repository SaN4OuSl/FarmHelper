package com.example.farmhelper.service;

import com.example.farmhelper.entity.ActionType;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    void importHarvestList(MultipartFile file);

    void importSellInvoices(MultipartFile file);

    byte[] exportTransactions(Long startDate, Long endDate, ActionType actionType);

    byte[] exportCrops(String year);

    byte[] exportExampleHarvests();

    byte[] exportExampleSaleInvoices();

}
