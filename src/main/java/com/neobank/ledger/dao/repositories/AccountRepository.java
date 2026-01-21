package com.neobank.ledger.dao.repositories;

import com.neobank.ledger.dao.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAlias(String alias);
    Long deleteByAlias(String alias);
}
