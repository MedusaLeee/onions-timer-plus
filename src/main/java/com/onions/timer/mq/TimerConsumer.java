package com.onions.timer.mq;

import com.onions.timer.service.TimerService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TimerConsumer {
    private Logger log = LoggerFactory.getLogger(TimerConsumer.class);
    @Autowired
    private TimerService timerService;

    @RabbitListener(queues = "timerProducerQ")
    public void ack(Message message, Channel channel) throws IOException {
        String messageBody = new String(message.getBody());
        timerService.setJob(messageBody);
        // 采用手动应答模式, 手动确认应答更为安全稳定
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        log.info("receive: " + new String(message.getBody()));
    }
}
