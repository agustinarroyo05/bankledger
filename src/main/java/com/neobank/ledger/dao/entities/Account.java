package com.neobank.ledger.dao.entities;

import com.neobank.ledger.exception.InvalidTransactionException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Accounts")
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    private String alias;
    private BigDecimal balance;

    public void debit(BigDecimal ammount){
        if (balance.compareTo(ammount) >= 0 ) {
            balance = balance.add(ammount.negate());
            return;
        }
        throw new InvalidTransactionException("Insufficient amount");
    }

    public void credit(BigDecimal ammount){
        balance = balance.add(ammount);
    }

}
