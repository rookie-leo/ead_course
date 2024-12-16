package com.ead.course.consumers;

import com.ead.course.dtos.UserEventRecordDto;
import com.ead.course.enums.ActionType;
import com.ead.course.services.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {

    final UserService userService;

    public UserConsumer(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.userEventQueue.name}", durable = "true"),
            exchange = @Exchange(value = "${ead.broker.exchange.userEventExchange}",
                    type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")
    ))
    public void listenUserEvent(@Payload UserEventRecordDto userEventRecordDto) {
        var userModel = userEventRecordDto.convertToUserModel();

        switch (ActionType.valueOf(userEventRecordDto.actionType())) {
            case CREATE, UPDATE -> userService.save(userModel);
            case DELETE -> userService.delete(userModel.getUserId());
        }
    }
}
