package com.neobank.ledger.dao.repositories;

import com.neobank.ledger.dao.entities.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {

    List<BankTransaction> findByFromAccountId(Long fromAccountId);
}
