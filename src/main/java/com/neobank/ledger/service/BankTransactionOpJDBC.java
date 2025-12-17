package com.neobank.ledger.service;

import com.neobank.ledger.dao.entities.Account;
import com.neobank.ledger.dto.BankTransactionDTO;
import com.neobank.ledger.mappers.BankTransactionMapper;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;

public class BankTransactionOpJDBC {

    private BankTransactionMapper bankTransactionMapper;
    private JdbcTemplate jdbcTemplate;


    public BankTransactionOpJDBC(BankTransactionMapper bankTransactionMapper,
                                 JdbcTemplate jdbcTemplate){
        this.bankTransactionMapper = bankTransactionMapper;
        this.jdbcTemplate = jdbcTemplate;

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BankTransactionDTO createTransaction(BankTransactionDTO bankTransactionDTO) {

        Account from = readAccount(bankTransactionDTO.fromAccount());
        Account to   = readAccount(bankTransactionDTO.toAccount());


        if (from.getBalance().compareTo(bankTransactionDTO.amount()) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }


        updateBalanceOptimistic(
                from.getId(),
                from.getBalance().subtract(bankTransactionDTO.amount()),
                from.getVersion()
        );


        updateBalanceOptimistic(
                to.getId(),
                to.getBalance().add(bankTransactionDTO.amount()),
                to.getVersion()
        );
        return createBankTransaction(bankTransactionDTO);

    }

    private Account readAccount(Long id) {
        return jdbcTemplate.queryForObject(
                """
                SELECT id, balance, version, alias
                FROM account
                WHERE id = ?
                """,
                (rs, i) -> {Account account = new Account();
                    account.setId(rs.getLong("id"));
                    account.setVersion(rs.getLong("version"));
                    account.setAlias(rs.getString("alias"));
                    account.setBalance(rs.getBigDecimal("balance"));
                    return account;
                },
                id
        );
    }

    private void updateBalanceOptimistic(
            Long accountId,
            BigDecimal newBalance,
            Long expectedVersion) {

        int updated = jdbcTemplate.update(
                """
                UPDATE account
                SET balance = ?, version = version + 1
                WHERE id = ? AND version = ?
                """,
                newBalance,
                accountId,
                expectedVersion
        );

        if (updated == 0) {
            throw new OptimisticLockingFailureException(
                    "Concurrent update detected for account " + accountId
            );
        }
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