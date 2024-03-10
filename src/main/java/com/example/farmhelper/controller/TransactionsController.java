package com.example.farmhelper.controller;

import com.example.farmhelper.model.response.TransactionResponse;
import com.example.farmhelper.service.TransactionService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionsController {

    private TransactionService transactionService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
}
