package com.neobank.ledger.service;

import com.neobank.ledger.dao.entities.Account;
import com.neobank.ledger.dao.entities.BankTransaction;
import com.neobank.ledger.dao.repositories.AccountRepository;
import com.neobank.ledger.dao.repositories.BankTransactionRepository;
import com.neobank.ledger.dto.BankTransactionDTO;
import com.neobank.ledger.exception.InvalidTransactionException;
import com.neobank.ledger.mappers.BankTransactionMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankTransactionService {

    private final BankTransactionMapper bankTransactionMapper;
    private final BankTransactionRepository bankTransactionRepository;
    private final AccountRepository accountRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BankTransactionService(BankTransactionMapper bankTransactionMapper,
                                  BankTransactionRepository bankTransactionRepository,
                                  EntityManager entityManager,
                                  AccountRepository accountRepository,
                                  ApplicationEventPublisher eventPublisher) {

        this.bankTransactionMapper = bankTransactionMapper;
        this.bankTransactionRepository = bankTransactionRepository;
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BankTransactionDTO createTransaction(BankTransactionDTO bankTransactionDTO){

        BankTransactionDTO result;

        try {
            if (bankTransactionDTO.fromAccountAlias().equals(bankTransactionDTO.toAccountAlias()))
                throw new InvalidTransactionException("From Account is the same as toAccount");

            Account fromAccount = accountRepository.findByAlias(bankTransactionDTO.fromAccountAlias()).
                    orElseThrow(() -> new InvalidTransactionException("Invalid From account"));
            Account toAccount = accountRepository.findByAlias(bankTransactionDTO.toAccountAlias()).
                    orElseThrow(() -> new InvalidTransactionException("Invalid To account"));

            fromAccount.debit(bankTransactionDTO.amount());
            toAccount.credit(bankTransactionDTO.amount());

            BankTransaction bankTransaction  = bankTransactionMapper.toBankTransaction(bankTransactionDTO, fromAccount, toAccount);
            bankTransactionRepository.save(bankTransaction);

            result  = new BankTransactionDTO(bankTransactionDTO, bankTransaction.getCreatedAt());

            eventPublisher.publishEvent(result);
        }
        catch(OptimisticLockException oe) {
            throw new InvalidTransactionException("This transaction was blocked, retry later");
        }

        return result;

    }

    public List<BankTransactionDTO> getTransactions(Long fromAccountId){
        return bankTransactionRepository.findByFromAccountId(fromAccountId)
                .stream()
                .map(bankTransactionMapper::toBankTransactionDTO)
                .collect(Collectors.toList());
    }
    public List<BankTransactionDTO> getTransactions(){
        return bankTransactionRepository.findAll()
                .stream()
                .map(bankTransactionMapper::toBankTransactionDTO)
                .collect(Collectors.toList());
    }
}
