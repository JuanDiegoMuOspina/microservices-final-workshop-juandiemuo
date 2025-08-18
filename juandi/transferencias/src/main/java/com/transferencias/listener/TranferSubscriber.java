package com.transferencias.listener;

import com.transferencias.dto.Transaction;
import com.transferencias.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TranferSubscriber {

    private final TransferService transferService;

    @RabbitListener(queues = "transfer-queue")
    public void receiveCart(Transaction transaction) {
        transferService.saveTransferencia(transaction).subscribe();
    }

}
