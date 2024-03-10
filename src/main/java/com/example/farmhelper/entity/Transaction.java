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
@Table(name = "transactions")
public class Transaction {
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @Getter
    @Setter
    private User user;

    @Column
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    @ToString.Include
    private ActionType actionType;

    @Column(name = "amount_after_action")
    @Getter
    @Setter
    private Double amountAfterAction;

    @Column(name = "amount_in_operation")
    @Getter
    @Setter
    private Double amountInOperation;

    @Column(name = "transactionPrice")
    @Getter
    @Setter
    private Double transactionPrice;

    @Column(name = "date_of_transaction")
    @Getter
    @Setter
    private Timestamp dateOfTransaction;

    @Column(name = "description", length = 1024)
    @Getter
    @Setter
    private String description;
}
