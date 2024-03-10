package com.example.farmhelper.service.impl;

import com.example.farmhelper.entity.ActionType;
import com.example.farmhelper.entity.Crop;
import com.example.farmhelper.entity.Field;
import com.example.farmhelper.entity.FieldHarvest;
import com.example.farmhelper.entity.Harvest;
import com.example.farmhelper.entity.InvoiceStatus;
import com.example.farmhelper.entity.SaleInvoice;
import com.example.farmhelper.entity.Transaction;
import com.example.farmhelper.exception.ImportException;
import com.example.farmhelper.exception.ResourceNotFoundException;
import com.example.farmhelper.model.request.HarvestRequest;
import com.example.farmhelper.repository.SaleInvoiceRepository;
import com.example.farmhelper.repository.TransactionRepository;
import com.example.farmhelper.service.CropService;
import com.example.farmhelper.service.FieldService;
import com.example.farmhelper.service.FileService;
import com.example.farmhelper.service.HarvestService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private static final String PATH_TO_HARVESTS_EXAMPLE = "/files/harvest-import.xlsx";

    private static final String PATH_TO_SALE_INVOICES_EXAMPLE = "/files/sale-invoices.xlsx";


    private TransactionRepository transactionRepository;

    private SaleInvoiceRepository saleInvoiceRepository;

    private CropService cropService;

    private HarvestService harvestService;

    private FieldService fieldService;

    private static final Long OLD_DATE_TIMESTAMP = 1656288000L;

    @Override
    @Transactional
    public void importHarvestList(MultipartFile file) {
        log.info("Method importHarvestList() started");
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Sheet sheet = workbook.getSheetAt(0);
        sheet.forEach(row -> {
            if (row.getRowNum() == 0) {
                return;
            }
            createHarvestFromRow(row);
        });
        log.info("Method importHarvestList() finished successfully");
    }

    private void createHarvestFromRow(Row row) {
        String cropName;
        try {
            cropName = row.getCell(0).getStringCellValue().trim();
        } catch (RuntimeException e) {
            throw new ImportException("Harvest",
                "The first two columns must not have null values.");
        }

        String monthAndYearOfCollection = row.getCell(1).getStringCellValue().trim();
        Pattern pattern = Pattern.compile("^[0-9]{4}-(0[1-9]|1[0-2])$");
        if (!pattern.matcher(monthAndYearOfCollection).matches()) {
            throw new ImportException(monthAndYearOfCollection, "Must be in format YYYY-MM");
        }

        double amount = row.getCell(2).getNumericCellValue();
        if (amount <= 0) {
            throw new ImportException(cropName + " " + monthAndYearOfCollection,
                "Amount should be positive");
        }

        String fields = row.getCell(3).getStringCellValue();
        Set<Long> fieldIds = Arrays.stream(fields.split(","))
            .map(String::trim)
            .map(this::getFieldIdByName)
            .collect(Collectors.toSet());

        Crop crop = cropService.getCropByName(cropName);
        HarvestRequest harvestRequest =
            new HarvestRequest(crop.getId(), amount, monthAndYearOfCollection, fieldIds);

        harvestService.createHarvest(harvestRequest);
    }

    private Long getFieldIdByName(String fieldName) {
        return fieldService.getFieldByName(fieldName).getId();
    }

    @Override
    @Transactional
    public void importSellInvoices(MultipartFile file) {
        log.info("Method importSellInvoices() started");
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Sheet sheet = workbook.getSheetAt(0);
        sheet.forEach(row -> {
            if (row.getRowNum() == 0) {
                return;
            }
            createInvoiceFromRow(row);
        });
        log.info("Method importSellInvoices() finished successfully");
    }

    private void createInvoiceFromRow(Row row) {
        String cropName;
        String monthAndYearOfCollection;
        try {
            cropName = row.getCell(0).getStringCellValue();
            monthAndYearOfCollection = row.getCell(1).getStringCellValue();
        } catch (RuntimeException e) {
            throw new ImportException("Harvest",
                "The first two columns must not have null values.");
        }
        double unitPrice = row.getCell(2).getNumericCellValue();
        if (unitPrice < 0) {
            throw new ImportException(cropName + " " + monthAndYearOfCollection,
                "Unit price should be positive");
        }
        double amount = row.getCell(3).getNumericCellValue();
        if (amount <= 0) {
            throw new ImportException(cropName + " " + monthAndYearOfCollection,
                "Amount should be positive");
        }
        try {
            String description = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "";

            Harvest harvest =
                harvestService.getHarvestByCropNameAndMonthAndYear(cropName,
                    monthAndYearOfCollection);
            SaleInvoice saleInvoice = SaleInvoice.builder()
                .harvest(harvest)
                .amount(amount)
                .unitPrice(unitPrice)
                .creationDate(Timestamp.from(Instant.now()))
                .description(description)
                .invoiceStatus(InvoiceStatus.CREATED)
                .build();
            saleInvoiceRepository.save(saleInvoice);
        } catch (RuntimeException e) {
            throw new ImportException(cropName + " " + monthAndYearOfCollection,
                "Check the entered data for this harvest");
        }
    }

    @Override
    public byte[] exportTransactions(Long startDateLong, Long endDateLong,
                                     ActionType actionType) {
        log.info("Method exportTransactions() started");
        try (
            XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet("Transactions");
            String[] headers = {"ID", "Harvest", "Username", "Action Type", "Amount After Action",
                "Amount In Operation", "Transaction Price", "Date Of Transaction", "Description"};

            Row headerRow = sheet.createRow(0);
            IntStream.range(0, headers.length)
                .forEach(i -> headerRow.createCell(i).setCellValue(headers[i]));

            Timestamp startDate = startDateLong == null ? new Timestamp(OLD_DATE_TIMESTAMP) :
                new Timestamp(startDateLong);
            Timestamp endDate = endDateLong == null ? Timestamp.from(Instant.now()) :
                new Timestamp(endDateLong);

            List<Transaction> transactions = (actionType != null)
                ? transactionRepository
                .findAllByActionTypeAndDateOfTransactionAfterAndDateOfTransactionBefore(
                    actionType, startDate, endDate) :
                transactionRepository.findAllByDateOfTransactionIsAfterAndDateOfTransactionIsBefore(
                    startDate, endDate);

            IntStream.range(0, transactions.size()).forEach(i -> {
                Transaction transaction = transactions.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(transaction.getId());
                row.createCell(1).setCellValue(transaction.getHarvest().getCrop().getName() + " "
                    + transaction.getHarvest().getMonthYearOfCollection());
                row.createCell(2).setCellValue(transaction.getUser().getId());
                row.createCell(3).setCellValue(transaction.getActionType().toString());
                row.createCell(4).setCellValue(transaction.getAmountAfterAction());
                row.createCell(5).setCellValue(transaction.getAmountInOperation());
                row.createCell(6).setCellValue(transaction.getTransactionPrice() == null ? "N/A" :
                    transaction.getTransactionPrice().toString());
                row.createCell(7).setCellValue(transaction.getDateOfTransaction().toString());
                row.createCell(8).setCellValue(transaction.getDescription() == null ? "" :
                    transaction.getDescription());
            });

            IntStream.range(0, headers.length).forEach(sheet::autoSizeColumn);

            workbook.write(baos);
            log.info("Method exportTransactions() finished successfully");
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export transactions to Excel file.");
            throw new RuntimeException("Failed to export transactions to Excel file.", e);
        }
    }


    @Override
    public byte[] exportCrops(String year) {
        log.info("Method exportCrops() started");
        try (
            XSSFWorkbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet("Crops");
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("Harvested crops for " + year);

            String[] headers = {"Crop name", "Total field size", "Total amount"};
            Row headerRow = sheet.createRow(1);
            IntStream.range(0, headers.length)
                .forEach(i -> headerRow.createCell(i).setCellValue(headers[i]));

            List<Crop> crops = cropService.getAllCrops("");

            AtomicInteger rowCounter = new AtomicInteger(2);
            crops.forEach(crop -> {
                List<Harvest> harvests = crop.getHarvests().stream()
                    .filter(h -> h.getMonthYearOfCollection().contains(year))
                    .toList();

                double totalFieldSize = harvests.stream()
                    .flatMap(harvest -> harvest.getFieldHarvests().stream())
                    .map(FieldHarvest::getField)
                    .mapToDouble(Field::getFieldSize)
                    .sum();

                double totalAmount = harvests.stream()
                    .flatMap(harvest -> harvest.getTransactions().stream())
                    .filter(transaction -> transaction.getActionType() == ActionType.ADD)
                    .mapToDouble(Transaction::getAmountInOperation)
                    .sum();

                Row row = sheet.createRow(rowCounter.getAndIncrement());
                row.createCell(0).setCellValue(crop.getName());
                row.createCell(1).setCellValue(totalFieldSize);
                row.createCell(2).setCellValue(totalAmount);
            });
            IntStream.range(0, headers.length).forEach(sheet::autoSizeColumn);

            workbook.write(baos);
            log.info("Method exportCrops() finished successfully");
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export crops to Excel file.");
            throw new RuntimeException("Failed to export crops to Excel file.", e);
        }
    }

    @Override
    public byte[] exportExampleHarvests() {
        log.info("Method exportExampleHarvests() started");
        try (InputStream inputStream = getClass().getResourceAsStream(PATH_TO_HARVESTS_EXAMPLE)) {
            byte[] exportHarvestsExample = Objects.requireNonNull(inputStream).readAllBytes();
            log.info("Method exportExampleHarvests() finished successfully, returned value: {}",
                PATH_TO_HARVESTS_EXAMPLE);
            return exportHarvestsExample;
        } catch (IOException e) {
            log.error("Example file was not found in this path: {}", PATH_TO_HARVESTS_EXAMPLE);
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public byte[] exportExampleSaleInvoices() {
        log.info("Method exportExampleSaleInvoices() started");
        try (InputStream inputStream = getClass().getResourceAsStream(
            PATH_TO_SALE_INVOICES_EXAMPLE)) {
            byte[] exportSaleInvoices = Objects.requireNonNull(inputStream).readAllBytes();
            log.info("Method exportExampleSaleInvoices() finished successfully, returned value: {}",
                PATH_TO_SALE_INVOICES_EXAMPLE);
            return exportSaleInvoices;
        } catch (IOException e) {
            log.error("Example file was not found in this path: {}", PATH_TO_SALE_INVOICES_EXAMPLE);
            throw new ResourceNotFoundException();
        }
    }
}
