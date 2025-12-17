package com.neobank.ledger;

import com.neobank.ledger.dao.entities.Account;
import com.neobank.ledger.dao.entities.BankTransaction;
import com.neobank.ledger.dao.repositories.AccountRepository;
import com.neobank.ledger.dao.repositories.BankTransactionRepository;
import com.neobank.ledger.dto.BankTransactionDTO;
import com.neobank.ledger.exception.InvalidTransactionException;
import com.neobank.ledger.mappers.BankTransactionMapper;
import com.neobank.ledger.service.BankTransactionService;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankTransactionServiceTest {

    @Mock
    private BankTransactionMapper bankTransactionMapper;

    @Mock
    private BankTransactionRepository bankTransactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BankTransactionService bankTransactionService;

    @Test
    void createTransaction_ok() {

        BankTransactionDTO dto =
                new BankTransactionDTO(1L, 2L, BigDecimal.TEN, null);

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setBalance(BigDecimal.valueOf(100));
        Account toAccount   = new Account();
        toAccount.setId(2L);
        toAccount.setBalance(BigDecimal.valueOf(50));

        BankTransaction entity = new BankTransaction();
        Instant now = Instant.now();
        entity.setCreatedAt(now);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(bankTransactionMapper.toBankTransaction(dto, fromAccount, toAccount))
                .thenReturn(entity);

        BankTransactionDTO result = bankTransactionService.createTransaction(dto);

        assertNotNull(result);
        assertEquals(now, result.createdAt());

        verify(bankTransactionRepository).save(entity);
        verify(eventPublisher).publishEvent(any(BankTransactionDTO.class));
    }

    @Test
    void createTransaction_sameAccount_throwsException() {

        BankTransactionDTO dto =
                new BankTransactionDTO(1L, 1L, BigDecimal.TEN, null);

        InvalidTransactionException ex =
                assertThrows(InvalidTransactionException.class,
                        () -> bankTransactionService.createTransaction(dto));

        assertEquals("From Account is the same as toAccount", ex.getMessage());

        verifyNoInteractions(accountRepository);
        verifyNoInteractions(bankTransactionRepository);
    }

    @Test
    void createTransaction_invalidFromAccount() {

        BankTransactionDTO dto =
                new BankTransactionDTO(1L, 2L, BigDecimal.TEN, null);

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        InvalidTransactionException ex =
                assertThrows(InvalidTransactionException.class,
                        () -> bankTransactionService.createTransaction(dto));

        assertEquals("Invalid From account", ex.getMessage());
    }

    @Test
    void createTransaction_invalidToAccount() {

        BankTransactionDTO dto =
                new BankTransactionDTO(1L, 2L, BigDecimal.TEN, null);

        Account account = new Account();
        account.setId(1L);
        account.setBalance(BigDecimal.TEN);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));
        when(accountRepository.findById(2L))
                .thenReturn(Optional.empty());

        InvalidTransactionException ex =
                assertThrows(InvalidTransactionException.class,
                        () -> bankTransactionService.createTransaction(dto));

        assertEquals("Invalid To account", ex.getMessage());
    }
    @Test
    void createTransaction_optimisticLockException() {

        BankTransactionDTO dto =
                new BankTransactionDTO(1L, 2L, BigDecimal.TEN, null);

        Account fromAccount = new Account();
        fromAccount.setId(1L);
        fromAccount.setBalance(BigDecimal.valueOf(100));
        Account toAccount  = new Account();
        toAccount.setId(2L);
        toAccount.setBalance(BigDecimal.valueOf(50));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(bankTransactionRepository.save(any()))
                .thenThrow(new OptimisticLockException());

        InvalidTransactionException ex =
                assertThrows(InvalidTransactionException.class,
                        () -> bankTransactionService.createTransaction(dto));

        assertEquals("This transaction was blocked, retry later", ex.getMessage());
    }

    @Test
    void getTransactions_ok() {

        BankTransaction bt1 = new BankTransaction();
        BankTransaction bt2 = new BankTransaction();

        when(bankTransactionRepository.findByFromAccountId(1L))
                .thenReturn(List.of(bt1, bt2));

        when(bankTransactionMapper.toBankTransactionDTO(bt1))
                .thenReturn(new BankTransactionDTO(1L, 2L, BigDecimal.TEN, null));
        when(bankTransactionMapper.toBankTransactionDTO(bt2))
                .thenReturn(new BankTransactionDTO(1L, 3L, BigDecimal.ONE, null));

        List<BankTransactionDTO> result = bankTransactionService.getTransactions(1L);

        assertEquals(2, result.size());
    }
}
