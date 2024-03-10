package com.example.farmhelper.repository;

import com.example.farmhelper.entity.ActionType;
import com.example.farmhelper.entity.Transaction;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByActionTypeAndDateOfTransactionAfterAndDateOfTransactionBefore(
        ActionType actionType, Timestamp startDate, Timestamp endDate);

    List<Transaction> findAllByDateOfTransactionIsAfterAndDateOfTransactionIsBefore(
        Timestamp startDate, Timestamp endDate);
}
