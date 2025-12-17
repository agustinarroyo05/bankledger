package com.neobank.ledger.dao.repositories;

import com.neobank.ledger.dao.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
