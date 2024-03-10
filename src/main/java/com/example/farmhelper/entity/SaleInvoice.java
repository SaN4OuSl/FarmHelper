package com.example.farmhelper.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sale_invoice")
public class SaleInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @ManyToOne
    @JoinColumn(name = "harvest_id")
    @JsonIgnore
    @Getter
    @Setter
    private Harvest harvest;

    @Column(name = "unit_price", nullable = false)
    @Getter
    @Setter
    private Double unitPrice;

    @Column(name = "amount", nullable = false)
    @Getter
    @Setter
    private Double amount;

    @Column(name = "description", length = 1024)
    @Getter
    @Setter
    private String description;

    @Column(name = "creation_date")
    @Getter
    @Setter
    private Timestamp creationDate;

    @Column(name = "completion_date")
    @Getter
    @Setter
    private Timestamp completionDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    @ToString.Include
    private InvoiceStatus invoiceStatus;
}
