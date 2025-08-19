package com.transacciones.service.client;

import com.transacciones.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TranOrdersPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TranOrdersPublisher tranOrdersPublisher;

    @Test
    @DisplayName("publishCartCreatedEvent_shouldCallRabbitTemplateWithCorrectArguments")
    void publishCartCreatedEvent_shouldCallRabbitTemplateWithCorrectArguments() {
        // Arrange (Given)
        Transaction transactionEvent = new Transaction();
        transactionEvent.setId(1L);
        transactionEvent.setTransferId("EVENT-001");
        transactionEvent.setAmount(new BigDecimal("99.99"));

        String expectedExchange = "transfer-exchange";
        String expectedRoutingKey = "transfer-routing-key";

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Act (When)
        tranOrdersPublisher.publishCartCreatedEvent(transactionEvent);

        // Assert (Then)
        verify(rabbitTemplate).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), transactionCaptor.capture());

        assertEquals(expectedExchange, exchangeCaptor.getValue());
        assertEquals(expectedRoutingKey, routingKeyCaptor.getValue());
        assertEquals(transactionEvent, transactionCaptor.getValue());
    }
}
