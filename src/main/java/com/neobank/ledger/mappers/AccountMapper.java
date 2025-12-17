package com.neobank.ledger.mappers;

import com.neobank.ledger.dao.entities.Account;
import com.neobank.ledger.dto.AccountDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toAccount(AccountDTO accountDTO);
}

