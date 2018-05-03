package com.leyou.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-27 19:20
 **/
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitmqConfig {

    @Bean
    public TopicExchange exchange(RabbitProperties properties) {
        // 读取配置文件中的默认路由名称
        TopicExchange exchange = new TopicExchange(properties.getTemplate().getExchange());
        exchange.setIgnoreDeclarationExceptions(true);
        return exchange;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, RabbitProperties properties) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        // 设置失败重发策略
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(5000);// 重试周期
        backOffPolicy.setMultiplier(2);// 递增倍数
        backOffPolicy.setMaxInterval(30000);// 最大周期
        retryTemplate.setBackOffPolicy(backOffPolicy);

        template.setRetryTemplate(retryTemplate);
        template.setExchange(properties.getTemplate().getExchange());
        return template;
    }


}
