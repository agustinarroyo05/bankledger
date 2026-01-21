package com.neobank.ledger.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountDTO(
            Long id,
            @NotNull(message="Alias must be not null" )String alias,
            BigDecimal balance
){}
