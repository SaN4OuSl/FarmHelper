package com.example.farmhelper.service;

import com.example.farmhelper.entity.ActionType;
import com.example.farmhelper.entity.Harvest;
import com.example.farmhelper.entity.Transaction;
import com.example.farmhelper.model.response.TransactionResponse;
import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Harvest harvest, ActionType actionType, Double amountInOperation,
                                  Double transactionPrice, String description);

    List<TransactionResponse> getAllTransactions();
}
