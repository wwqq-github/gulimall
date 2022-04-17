package com.atguigu.order.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {
    //队列
    @Bean
    public Queue queue() {
        //name  队列的名字
        //durable   是否持久化
        //exclusive  是否排他
        //autoDelete 是否自动删除
        // public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments)
        return new Queue("order.queue",true,false,false,null);
    }

    // 死信队列
    @Bean
    public Queue deadQueue() {
        //name  队列的名字
        //durable   是否持久化
        //exclusive  是否排他
        //autoDelete 是否自动删除
        // public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments)
        Map<String, Object> arguments =new HashMap<>();

//         <entry key="x-dead-letter-exchange" value="myDLX"/>
//        <entry key="x-dead-letter-routing-key" value="dlqRK"/>
//<entry key="x-message-ttl" value="100" value-type="java.lang.Long"/>
        arguments.put("x-dead-letter-exchange","exchange");
        arguments.put("x-dead-letter-routing-key","order.exchange");
        arguments.put("x-message-ttl",100000);
        return new Queue("dead.queue",false,false,false,arguments);
    }
    //队列的交换机
    @Bean
    public Exchange exchange(){
        //name  交换机的名字
        //durable  是否持久化
        //autoDelete 是否自动删除
        //public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
        return new DirectExchange("exchange",true,false,null);
    }


    @Bean
    public Binding binding(){
        //destination   目的地
        //destinationType  目的地类型
        //exchange    交换机
        //routingKey   路由键
        //public Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments) {
        return new Binding("order.queue", Binding.DestinationType.QUEUE,"exchange","order.exchange",null);
    }

    @Bean
    public Binding deadBinding(){
        //destination   目的地
        //destinationType  目的地类型
        //exchange    交换机
        //routingKey   路由键
        //public Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments) {
        return new Binding("dead.queue", Binding.DestinationType.QUEUE,"exchange","dead.exchange",null);
    }

}
