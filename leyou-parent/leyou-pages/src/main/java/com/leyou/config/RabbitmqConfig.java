package com.leyou.config;

import org.springframework.amqp.core.*;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-27 19:20
 **/
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitmqConfig {

    public final static String PAGE_QUEUE = "leyou_static_page_queue";

    @Bean
    public Queue pageQueue(){
        return new Queue(PAGE_QUEUE);
    }

    @Bean
    public TopicExchange exchange(RabbitProperties properties) {
        // 读取配置文件中的默认路由名称
        return new TopicExchange(properties.getTemplate().getExchange());
    }

    @Bean
    public Binding binding(Queue pageQueue, TopicExchange exchange){
        return BindingBuilder.bind(pageQueue).to(exchange).with("item.#");
    }

}
