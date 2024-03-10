package com.example.farmhelper.service.impl;

import com.example.farmhelper.entity.Harvest;
import com.example.farmhelper.entity.InvoiceStatus;
import com.example.farmhelper.entity.SaleInvoice;
import com.example.farmhelper.exception.InvoiceIsAlreadyCompleted;
import com.example.farmhelper.exception.ResourceNotFoundException;
import com.example.farmhelper.mapper.SaveInvoiceMapper;
import com.example.farmhelper.model.request.SaleInvoiceRequest;
import com.example.farmhelper.model.response.SaleInvoiceResponse;
import com.example.farmhelper.repository.SaleInvoiceRepository;
import com.example.farmhelper.service.HarvestService;
import com.example.farmhelper.service.SaleInvoiceService;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SaleInvoiceServiceImpl implements SaleInvoiceService {

    private SaleInvoiceRepository saleInvoiceRepository;

    private HarvestService harvestService;

    @Override
    public SaleInvoiceResponse createSaleInvoice(SaleInvoiceRequest saleInvoiceRequest) {
        log.info("Method createSaleInvoice() started with {}", saleInvoiceRequest);
        Harvest harvest = harvestService.getHarvestById(saleInvoiceRequest.getHarvestId());
        harvestService.validateAmount(harvest, saleInvoiceRequest.getAmount());
        SaleInvoice saleInvoice =
            SaleInvoice.builder().harvest(harvest).amount(saleInvoiceRequest.getAmount())
                .unitPrice(saleInvoiceRequest.getUnitPrice())
                .creationDate(Timestamp.from(Instant.now()))
                .description(saleInvoiceRequest.getDescription())
                .invoiceStatus(InvoiceStatus.CREATED).build();
        saleInvoice = saleInvoiceRepository.save(saleInvoice);
        SaleInvoiceResponse saleInvoiceResponse =
            SaveInvoiceMapper.INSTANCE.toSaleInvoiceResponse(saleInvoice);
        log.info("Method createSaleInvoice() finished successfully, returned value: {}",
            saleInvoiceResponse);
        return saleInvoiceResponse;
    }

    @Override
    public SaleInvoiceResponse updateSaleInvoice(Long id,
                                                 SaleInvoiceRequest updateSaleInvoiceRequest) {
        log.info("Method updateSaleInvoice() started with id = {} and {}", id,
            updateSaleInvoiceRequest);
        SaleInvoice saleInvoice = getSaleInvoiceById(id);
        validateSaleInvoiceStatus(saleInvoice);
        Harvest harvest = harvestService.getHarvestById(updateSaleInvoiceRequest.getHarvestId());
        harvestService.validateAmount(harvest, updateSaleInvoiceRequest.getAmount());
        saleInvoice.setHarvest(harvest);
        saleInvoice.setAmount(updateSaleInvoiceRequest.getAmount());
        saleInvoice.setUnitPrice(updateSaleInvoiceRequest.getUnitPrice());
        saleInvoice.setDescription(updateSaleInvoiceRequest.getDescription());
        saleInvoice = saleInvoiceRepository.save(saleInvoice);
        SaleInvoiceResponse saleInvoiceResponse =
            SaveInvoiceMapper.INSTANCE.toSaleInvoiceResponse(saleInvoice);
        log.info("Method updateSaleInvoice() finished successfully, returned value: {}",
            saleInvoice);
        return saleInvoiceResponse;
    }

    @Override
    public List<SaleInvoiceResponse> getAllSaleInvoices() {
        List<SaleInvoiceResponse> saleInvoiceResponses;
        try {
            List<SaleInvoice> saleInvoices = saleInvoiceRepository.findAll();

            saleInvoiceResponses = saleInvoices.stream()
                .map(SaveInvoiceMapper.INSTANCE::toSaleInvoiceResponse)
                .collect(Collectors.toList());

            log.info("Method getAllSaleInvoices() finished successfully, returned value: {}",
                saleInvoiceResponses.size());
        } catch (Exception e) {
            log.error("An error occurred while retrieving harvests: {}",
                e.getMessage());
            throw new RuntimeException("Error retrieving harvests", e);
        }
        return saleInvoiceResponses;
    }

    @Override
    @Transactional
    public SaleInvoiceResponse executeSaleInvoice(Long id) {
        log.info("Method executeSaleInvoice() started with id = {}", id);
        SaleInvoice saleInvoice = getSaleInvoiceById(id);
        validateSaleInvoiceStatus(saleInvoice);
        harvestService.sellAmountFromHarvest(saleInvoice.getHarvest(), saleInvoice.getAmount(),
            saleInvoice.getUnitPrice(), saleInvoice.getDescription());
        saleInvoice.setInvoiceStatus(InvoiceStatus.PROCESSED);
        saleInvoice.setCompletionDate(Timestamp.from(Instant.now()));
        saleInvoice = saleInvoiceRepository.save(saleInvoice);
        SaleInvoiceResponse saleInvoiceResponse =
            SaveInvoiceMapper.INSTANCE.toSaleInvoiceResponse(saleInvoice);
        log.info("Method executeSaleInvoice() finished successfully, returned value: {}",
            saleInvoice);
        return saleInvoiceResponse;
    }

    @Override
    public void deleteSaleInvoice(Long id) {
        log.info("Method deleteSaleInvoice() started with id = {}", id);
        SaleInvoice saleInvoice = getSaleInvoiceById(id);
        validateSaleInvoiceStatus(saleInvoice);
        saleInvoiceRepository.delete(saleInvoice);
        log.info("Method deleteSaleInvoice() finished successfully");
    }

    @Override
    public SaleInvoice getSaleInvoiceById(Long id) {
        log.info("Method getSaleInvoiceById() started with id = {}", id);
        SaleInvoice saleInvoice = saleInvoiceRepository.findById(id).orElseThrow(() -> {
            log.warn("SaleInvoice with id = {} not found", id);
            return new ResourceNotFoundException("SaleInvoice", id.toString());
        });
        log.info("Method getSaleInvoiceById() finished successfully, returned value: {}",
            saleInvoice);
        return saleInvoice;
    }

    private void validateSaleInvoiceStatus(SaleInvoice saleInvoice) {
        if (saleInvoice.getInvoiceStatus() == InvoiceStatus.PROCESSED) {
            throw new InvoiceIsAlreadyCompleted(saleInvoice.getId());
        }
    }
}
