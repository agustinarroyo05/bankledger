package com.neobank.ledger.dto;

import java.math.BigDecimal;

public record AccountDTO(
            String alias,
            BigDecimal balance
){}
