package com.neobank.ledger;

import com.neobank.ledger.dao.entities.Account;
import com.neobank.ledger.dao.repositories.AccountRepository;
import com.neobank.ledger.dto.AccountDTO;
import com.neobank.ledger.mappers.AccountMapper;
import com.neobank.ledger.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_ok() {
        // given
        AccountDTO dto =
                new AccountDTO(1L, "alias-test", new BigDecimal("1000"));

        Account account = new Account();

        when(accountMapper.toAccount(dto))
                .thenReturn(account);

        when(accountRepository.save(account))
                .thenReturn(account);

        // when
        AccountDTO result =
                accountService.createAccount(dto);

        // then
        assertNotNull(result);
        assertEquals(dto, result);

        verify(accountMapper, times(1))
                .toAccount(dto);

        verify(accountRepository, times(1))
                .save(account);

        verifyNoMoreInteractions(accountMapper, accountRepository);
    }
}

