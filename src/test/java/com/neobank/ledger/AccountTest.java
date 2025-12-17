package com.neobank.ledger;

import com.neobank.ledger.dao.entities.Account;
import com.neobank.ledger.exception.InvalidTransactionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

    @Test
    void debit_credit_ok(){
        Account debit = new Account();
        debit.setBalance(new BigDecimal(100));
        debit.debit(new BigDecimal(10));

        Account credit = new Account();
        credit.setBalance(new BigDecimal(100));
        credit.credit(new BigDecimal(10));

        assertEquals(debit.getBalance(), new BigDecimal(90));
        assertEquals(credit.getBalance(), new BigDecimal(110));
    }

    @Test
    void debit_fail(){
        Account debit = new Account();
        debit.setBalance(new BigDecimal(10));
        InvalidTransactionException ex =
                assertThrows(InvalidTransactionException.class,
                        () ->  debit.debit(new BigDecimal(100)));;

    }
}
