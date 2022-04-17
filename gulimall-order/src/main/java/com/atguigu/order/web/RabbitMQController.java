package com.atguigu.order.web;

import com.atguigu.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RabbitMQController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/rabbit")
    public String addRabbit(){
        System.out.println("发送消息");
        OrderEntity orderEntity =new OrderEntity();
        orderEntity.setPayType(1);
        orderEntity.setPayType(1);
        orderEntity.setId("fdasfsdafasdg");
        orderEntity.setMemberUsername("张三");
        rabbitTemplate.convertAndSend("exchange","dead.exchange",orderEntity);
        System.out.println("发送成功");
        return "成功";
    }

}
