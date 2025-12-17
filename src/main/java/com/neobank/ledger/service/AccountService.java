package com.neobank.ledger.service;

import com.neobank.ledger.dao.entities.Account;
import com.neobank.ledger.dao.repositories.AccountRepository;
import com.neobank.ledger.dto.AccountDTO;
import com.neobank.ledger.mappers.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    public AccountDTO createAccount(AccountDTO accountDto){
        Account account = accountMapper.toAccount(accountDto);
        accountRepository.save(account);
        return accountDto;
    }

}
