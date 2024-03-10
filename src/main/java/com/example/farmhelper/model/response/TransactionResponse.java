package com.example.farmhelper.model.response;

import com.example.farmhelper.entity.ActionType;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class TransactionResponse {
    private Long id;

    private String cropName;

    private String monthAndYearOfCollection;

    private String userInfo;

    private ActionType actionType;

    private Double amountAfterAction;

    private Double amountInOperation;

    private Double transactionPrice;

    private Timestamp dateOfTransaction;

    private String explanation;
}
