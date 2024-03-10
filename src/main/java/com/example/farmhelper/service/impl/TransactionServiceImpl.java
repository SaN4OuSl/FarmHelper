package com.example.farmhelper.service.impl;

import com.example.farmhelper.entity.ActionType;
import com.example.farmhelper.entity.Harvest;
import com.example.farmhelper.entity.Transaction;
import com.example.farmhelper.mapper.HarvestMapper;
import com.example.farmhelper.mapper.TransactionMapper;
import com.example.farmhelper.model.response.HarvestResponse;
import com.example.farmhelper.model.response.TransactionResponse;
import com.example.farmhelper.repository.TransactionRepository;
import com.example.farmhelper.service.TransactionService;
import com.example.farmhelper.service.UserService;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;

    private UserService userService;

    @Override
    public Transaction createTransaction(Harvest harvest, ActionType actionType,
                                         Double amountInOperation, Double transactionPrice,
                                         String description) {
        log.info("Method createTransaction() started");
        Transaction transaction =
            Transaction.builder().harvest(harvest).user(userService.getCurrentUser())
                .actionType(actionType).amountAfterAction(
                    harvest.getAmount()).amountInOperation(amountInOperation)
                .transactionPrice(transactionPrice).description(description).dateOfTransaction(
                    Timestamp.from(Instant.now())).build();
        transaction = transactionRepository.save(transaction);
        log.info("Method createTransaction() finished successfully, returned value: {}",
            transaction);
        return transaction;
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        log.info("Method getAllTransactions() started");
        List<TransactionResponse> transactionResponses;
        try {
            List<Transaction> transactions = transactionRepository.findAll();

            transactionResponses = transactions.stream()
                .map(TransactionMapper.INSTANCE::toTransactionsResponse)
                .collect(Collectors.toList());

            log.info("Method getAllTransactions() finished successfully, returned value: {}",
                transactionResponses.size());
        } catch (Exception e) {
            log.error("An error occurred while retrieving harvests: {}",
                e.getMessage());
            throw new RuntimeException("Error retrieving harvests", e);
        }
        return transactionResponses;
    }
}
