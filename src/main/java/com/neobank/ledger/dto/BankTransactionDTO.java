package com.neobank.ledger.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BankTransactionDTO(
        Long fromAccount,
        Long toAccount,
        BigDecimal amount,
        Instant createdAt
){
    public BankTransactionDTO(BankTransactionDTO dto, Instant instant){
        this(dto.fromAccount, dto.toAccount, dto.amount, instant);
    }
}
