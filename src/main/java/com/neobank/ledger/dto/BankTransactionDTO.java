package com.neobank.ledger.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record BankTransactionDTO(
        Long fromAccount,
        Long toAccount,
        @NotNull(message = "From account must be not null") String fromAccountAlias,
        @NotNull(message = "To account must be not null") String toAccountAlias,
        @NotNull(message = "Amount must not be null") BigDecimal amount,
        Instant createdAt
){
    public BankTransactionDTO(BankTransactionDTO dto, Instant instant){
        this(dto.fromAccount, dto.toAccount, dto.fromAccountAlias(), dto.toAccountAlias, dto.amount, instant);
    }
}
