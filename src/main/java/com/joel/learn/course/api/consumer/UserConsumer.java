package com.joel.learn.course.api.consumer;

import com.joel.learn.course.domain.dtos.UserEventDTO;
import com.joel.learn.course.domain.enums.ActionType;
import com.joel.learn.course.domain.models.User;
import com.joel.learn.course.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConsumer {

    private final UserService userService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${learn.broker.queue.userEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${learn.broker.exchange.userEventExchange}",
                    type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")
    ))
    public void listenUserEvent(@Payload UserEventDTO userEventDTO) {
        User user = eventToEntity(userEventDTO);
        ActionType actionType = ActionType.valueOf(userEventDTO.getActionType());

        switch (actionType) {
            case CREATE:
            case UPDATE:
                userService.save(user);
                break;
            case DELETE:
                userService.delete(user.getUserId());
        }
    }

    public User eventToEntity(UserEventDTO userEventDTO) {
        return User.builder()
                .userId(userEventDTO.getUserId())
                .fullName(userEventDTO.getFullName())
                .email(userEventDTO.getEmail())
                .userType(userEventDTO.getUserType())
                .cpf(userEventDTO.getCpf())
                .build();
    }

}
