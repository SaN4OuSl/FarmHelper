package com.example.farmhelper.repository;

import com.example.farmhelper.entity.SaleInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleInvoiceRepository extends JpaRepository<SaleInvoice, Long> {
}
