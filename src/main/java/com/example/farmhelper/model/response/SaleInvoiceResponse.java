package com.example.farmhelper.model.response;

import com.example.farmhelper.entity.InvoiceStatus;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class SaleInvoiceResponse {

    private Long id;

    private Long harvestId;

    private String harvestInfo;

    private Double amount;

    private Double unitPrice;

    private String description;

    private Timestamp creationDate;

    private Timestamp completionDate;

    private InvoiceStatus invoiceStatus;
}
