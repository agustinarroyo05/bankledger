package com.neobank.ledger.controller;

import com.neobank.ledger.dto.AccountDTO;
import com.neobank.ledger.dto.BankTransactionDTO;
import com.neobank.ledger.exception.InvalidTransactionException;
import com.neobank.ledger.service.AccountService;
import com.neobank.ledger.service.BankTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BankController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private BankTransactionService bankTransactionService;

    @PostMapping(value="/accounts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountDTO createAccount(@Valid @RequestBody AccountDTO account){
        return accountService.createAccount(account);
    }

    @PostMapping(value="/transactions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BankTransactionDTO createBankTransaction(@Valid @RequestBody BankTransactionDTO bankTransaction){
            return bankTransactionService.createTransaction(bankTransaction);
    }

    @GetMapping(value="/transactions/{accountId}")
    public List<BankTransactionDTO> getTransactions(@PathVariable Long accountId){
        return bankTransactionService.getTransactions(accountId);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<String> handleNotFound(
            InvalidTransactionException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage()
                );
    }

}
