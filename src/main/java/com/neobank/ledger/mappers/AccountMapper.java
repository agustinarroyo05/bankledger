package com.neobank.ledger.mappers;

import com.neobank.ledger.dao.entities.Account;
import com.neobank.ledger.dto.AccountDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    Account toAccount(AccountDTO accountDTO);
    List<AccountDTO> toAccountDTO(List<Account> accountDTO);
}

