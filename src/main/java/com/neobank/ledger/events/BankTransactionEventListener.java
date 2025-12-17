package com.neobank.ledger.events;

import com.neobank.ledger.configuration.EventConfig;
import com.neobank.ledger.dto.BankTransactionDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BankTransactionEventListener {

    private final RabbitTemplate rabbitTemplate;

    public BankTransactionEventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onEvent(BankTransactionDTO event) {
        rabbitTemplate.convertAndSend(
                EventConfig.EXCHANGE,
                EventConfig.ROUTING_KEY,
                event
        );;
    }
}