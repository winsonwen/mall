package com.atguigu.mall.order.controller;

import com.atguigu.mall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RabbitController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    @ResponseBody
    public String sendMq(@RequestParam(value = "num", defaultValue = "10") Integer num){
        System.out.println("sent");
        for(int i=0;i<num;i++){
            OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
            reasonEntity.setId(1L);
            rabbitTemplate.convertAndSend("hello-java-exchange","hello-java-queue",reasonEntity);

        }
        System.out.println("sent");
        return "ok";
    }
}
