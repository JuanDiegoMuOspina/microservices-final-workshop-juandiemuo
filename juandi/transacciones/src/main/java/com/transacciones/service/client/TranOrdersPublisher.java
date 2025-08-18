package com.transacciones.service.client;

import com.transacciones.model.Transaction;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class TranOrdersPublisher {
   private final RabbitTemplate rabbitTemplate;

   public TranOrdersPublisher(RabbitTemplate rabbitTemplate) {
     this.rabbitTemplate = rabbitTemplate;
   }

   public void publishCartCreatedEvent(Transaction tran) {
     rabbitTemplate.convertAndSend("transfer-exchange", "transfer-routing-key", tran);
     System.out.println("Transaction event published");
   }
}
