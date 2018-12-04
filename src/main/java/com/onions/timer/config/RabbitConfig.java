package com.onions.timer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue timerConsumerQ() {
        return new Queue("timerConsumerQ", true);
    }
    @Bean
    public Queue timerProducerQ() {
        return new Queue("timerProducerQ", true);
    }
}
