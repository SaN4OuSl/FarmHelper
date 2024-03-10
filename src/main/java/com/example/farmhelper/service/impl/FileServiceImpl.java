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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final String PATH_TO_HARVESTS_EXAMPLE = "/files/harvest-import.xlsx";
    private static final String PATH_TO_SALE_INVOICES_EXAMPLE = "/files/sale-invoices.xlsx";
    private static final Long OLD_DATE_TIMESTAMP = 1656288000L;

    private final TransactionRepository transactionRepository;
    private final SaleInvoiceRepository saleInvoiceRepository;
    private final CropService cropService;
    private final HarvestService harvestService;
    private final FieldService fieldService;

    @Autowired
    public FileServiceImpl(TransactionRepository transactionRepository,
                           SaleInvoiceRepository saleInvoiceRepository, CropService cropService,
                           HarvestService harvestService, FieldService fieldService) {
        this.transactionRepository = transactionRepository;
        this.saleInvoiceRepository = saleInvoiceRepository;
        this.cropService = cropService;
        this.harvestService = harvestService;
        this.fieldService = fieldService;
    }

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
        String cropName = row.getCell(0).getStringCellValue().trim();
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
        String cropName = row.getCell(0).getStringCellValue().trim();
        String monthAndYearOfCollection = row.getCell(1).getStringCellValue().trim();
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
        String description = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "";

        Harvest harvest =
            harvestService.getHarvestByCropNameAndMonthAndYear(cropName, monthAndYearOfCollection);
        SaleInvoice saleInvoice = SaleInvoice.builder()
            .harvest(harvest)
            .amount(amount)
            .unitPrice(unitPrice)
            .creationDate(Timestamp.from(Instant.now()))
            .description(description)
            .invoiceStatus(InvoiceStatus.CREATED)
            .build();
        saleInvoiceRepository.save(saleInvoice);
    }

    @Override
    public byte[] exportTransactions(Long startDateLong, Long endDateLong, ActionType actionType) {
        log.info("Method exportTransactions() started");
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Transactions");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle cellStyle = createCellStyle(workbook);

            String[] headers =
                {"ID", "Harvest", "Username", "Action Type", "Amount After Action, ce",
                    "Amount In Operation, ce", "Transaction Price, ₴", "Date Of Transaction",
                    "Description"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            Timestamp startDate = Optional.ofNullable(startDateLong)
                .map(Timestamp::new)
                .orElse(new Timestamp(OLD_DATE_TIMESTAMP));
            Timestamp endDate = Optional.ofNullable(endDateLong)
                .map(Timestamp::new)
                .orElse(Timestamp.from(Instant.now()));
            List<Transaction> transactions = Optional.ofNullable(actionType)
                .map(
                    at -> transactionRepository.findAllByActionTypeAndDateOfTransactionAfterAndDateOfTransactionBefore(
                        at, startDate, endDate))
                .orElseGet(
                    () -> transactionRepository.findAllByDateOfTransactionIsAfterAndDateOfTransactionIsBefore(
                        startDate, endDate));

            IntStream.range(0, transactions.size()).forEach(i -> {
                Transaction transaction = transactions.get(i);
                Row row = sheet.createRow(i + 1);
                fillTransactionRow(headers, row, cellStyle, transaction);
            });

            IntStream.range(0, headers.length).forEach(sheet::autoSizeColumn);
            workbook.write(baos);
            log.info("Method exportTransactions() finished successfully");
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export transactions to Excel file.", e);
            throw new RuntimeException("Failed to export transactions to Excel file.", e);
        }
    }

    private void fillTransactionRow(String[] headers, Row row, CellStyle cellStyle,
                                    Transaction transaction) {
        for (int j = 0; j < headers.length; j++) {
            Cell cell = row.createCell(j);
            cell.setCellStyle(cellStyle);
        }
        row.getCell(0).setCellValue(transaction.getId());
        row.getCell(1).setCellValue(transaction.getHarvest().getCrop().getName() + " " +
            transaction.getHarvest().getMonthYearOfCollection());
        row.getCell(2).setCellValue(transaction.getUser().getId());
        row.getCell(3).setCellValue(transaction.getActionType().toString());
        row.getCell(4).setCellValue(transaction.getAmountAfterAction());
        row.getCell(5).setCellValue(transaction.getAmountInOperation());
        row.getCell(6).setCellValue(transaction.getTransactionPrice() == null ? "N/A" :
            transaction.getTransactionPrice().toString());
        row.getCell(7).setCellValue(transaction.getDateOfTransaction().toString());
        row.getCell(8)
            .setCellValue(transaction.getDescription() == null ? "" : transaction.getDescription());
    }

    @Override
    public byte[] exportCrops(String year) {
        log.info("Method exportCrops() started");
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Crops");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle cellStyle = createCellStyle(workbook);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Harvested crops for " + year);

            String[] headers = {"Crop name", "Total field size, ha", "Total amount, ce"};
            Row headerRow = sheet.createRow(1);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Crop> crops = cropService.getAllCrops("");
            AtomicInteger rowCounter = new AtomicInteger(2);
            crops.forEach(crop -> {
                Row row = createCropRow(sheet, rowCounter.getAndIncrement(), cellStyle);
                fillCropRow(row, crop, year);
            });

            IntStream.range(0, headers.length).forEach(sheet::autoSizeColumn);
            workbook.write(baos);
            log.info("Method exportCrops() finished successfully");
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export crops to Excel file.", e);
            throw new RuntimeException("Failed to export crops to Excel file.", e);
        }
    }

    private Row createCropRow(Sheet sheet, int rowNum, CellStyle cellStyle) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < 3; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(cellStyle);
        }
        return row;
    }

    private void fillCropRow(Row row, Crop crop, String year) {
        double totalFieldSize = crop.getHarvests().stream()
            .filter(harvest -> harvest.getMonthYearOfCollection().contains(year))
            .flatMap(harvest -> harvest.getFieldHarvests().stream())
            .map(FieldHarvest::getField)
            .mapToDouble(Field::getFieldSize)
            .sum();

        double totalAmount = crop.getHarvests().stream()
            .filter(harvest -> harvest.getMonthYearOfCollection().contains(year))
            .flatMap(harvest -> harvest.getTransactions().stream())
            .mapToDouble(Transaction::getAmountInOperation)
            .sum();

        row.getCell(0).setCellValue(crop.getName());
        row.getCell(1).setCellValue(totalFieldSize);
        row.getCell(2).setCellValue(totalAmount);
    }

    @Override
    public byte[] exportExampleHarvests() {
        log.info("Method exportExampleHarvests() started");
        try (InputStream inputStream = getClass().getResourceAsStream(PATH_TO_HARVESTS_EXAMPLE)) {
            byte[] bytes = Objects.requireNonNull(inputStream).readAllBytes();
            log.info("Method exportExampleHarvests() finished successfully");
            return bytes;
        } catch (IOException e) {
            log.error("Example file was not found", e);
            throw new RuntimeException("Example file was not found", e);
        }
    }

    @Override
    public byte[] exportExampleSaleInvoices() {
        log.info("Method exportExampleSaleInvoices() started");
        try (InputStream inputStream = getClass().getResourceAsStream(
            PATH_TO_SALE_INVOICES_EXAMPLE)) {
            byte[] bytes = Objects.requireNonNull(inputStream).readAllBytes();
            log.info("Method exportExampleSaleInvoices() finished successfully");
            return bytes;
        } catch (IOException e) {
            log.error("Example file was not found", e);
            throw new RuntimeException("Example file was not found", e);
        }
    }

    private CellStyle createCellStyle(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        return cellStyle;
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        return headerStyle;
    }
}

