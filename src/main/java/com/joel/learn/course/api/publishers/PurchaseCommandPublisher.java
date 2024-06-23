package com.joel.learn.course.api.publishers;

import com.joel.learn.course.domain.dtos.PurchaseEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value(value = "${learn.broker.exchange.purchaseCommandExchange}")
    private String purchaseCommandExchange;

    @Value(value = "${learn.broker.key.purchaseCommandKey}")
    private String purchaseCommandKey;

    public void publishPurchaseEvent(PurchaseEventDTO purchaseEventDTO) {
        rabbitTemplate.convertAndSend(purchaseCommandExchange, purchaseCommandKey, purchaseEventDTO);
    }
}
