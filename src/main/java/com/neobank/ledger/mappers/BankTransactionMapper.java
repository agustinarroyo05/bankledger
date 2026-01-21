package com.neobank.ledger.mappers;

import com.neobank.ledger.dao.entities.Account;
import com.neobank.ledger.dao.entities.BankTransaction;
import com.neobank.ledger.dto.BankTransactionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankTransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fromAccount", source = "fromAccount")
    @Mapping(target = "toAccount", source = "toAccount")
    BankTransaction toBankTransaction(BankTransactionDTO bankTransactionDTO, Account fromAccount, Account toAccount);

    @Mapping(target = "fromAccount", ignore = true)
    @Mapping(target = "toAccount", ignore = true)
    @Mapping(target = "fromAccountAlias", source = "fromAccount.alias")
    @Mapping(target = "toAccountAlias", source = "toAccount.alias")
    BankTransactionDTO toBankTransactionDTO(BankTransaction bankTransaction);
}

