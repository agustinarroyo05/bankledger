package com.neobank.ledger.service;

import com.neobank.ledger.dto.BankTransactionDTO;
import com.neobank.ledger.mappers.BankTransactionMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;

public class BankTransactionJDBC {

    private BankTransactionMapper bankTransactionMapper;
    private JdbcTemplate jdbcTemplate;

    public BankTransactionJDBC(BankTransactionMapper bankTransactionMapper,
                               JdbcTemplate jdbcTemplate){
        this.bankTransactionMapper = bankTransactionMapper;
        this.jdbcTemplate = jdbcTemplate;

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BankTransactionDTO createTransaction(BankTransactionDTO bankTransactionDTO) {
        BigDecimal fromBalance = jdbcTemplate.queryForObject(
                "SELECT balance FROM account WHERE id = ?",
                BigDecimal.class,
                bankTransactionDTO.fromAccount()
        );

        if (fromBalance.compareTo(bankTransactionDTO.amount()) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        jdbcTemplate.update(
                "UPDATE account SET balance = balance - ? WHERE id = ?",
                bankTransactionDTO.amount(), bankTransactionDTO.fromAccount()
        );

        jdbcTemplate.update(
                "UPDATE account SET balance = balance + ? WHERE id = ?",
                bankTransactionDTO.amount(), bankTransactionDTO.toAccount()
        );

        return createBankTransaction(bankTransactionDTO);

    }

    private BankTransactionDTO createBankTransaction(BankTransactionDTO bankTransactionDTO) {

        Instant instant = Instant.now();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    """
                    INSERT INTO bank_transaction
                        (from_account_id, to_account_id, amount, created_at)
                    VALUES (?, ?, ?, ?)
                    """
            );
            ps.setLong(1, bankTransactionDTO.fromAccount());
            ps.setLong(2, bankTransactionDTO.toAccount());
            ps.setBigDecimal(3, bankTransactionDTO.amount());
            ps.setTimestamp(4, Timestamp.from(instant));
            return ps;
        });

        return new BankTransactionDTO(bankTransactionDTO, instant);
    }

}