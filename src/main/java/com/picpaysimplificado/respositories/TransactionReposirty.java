package com.picpaysimplificado.respositories;

import com.picpaysimplificado.domain.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionReposirty extends JpaRepository<Transaction, Long> {
}
