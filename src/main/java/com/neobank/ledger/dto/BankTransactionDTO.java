package com.neobank.ledger.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record BankTransactionDTO(
        @NotNull(message = "From account must be not null") Long fromAccount,
        @NotNull(message = "To account must be not null") Long toAccount,
        @NotNull(message = "Amount must not be null") BigDecimal amount,
        Instant createdAt
){
    public BankTransactionDTO(BankTransactionDTO dto, Instant instant){
        this(dto.fromAccount, dto.toAccount, dto.amount, instant);
    }
}
