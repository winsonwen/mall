package com.atguigu.mall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /*
    * 定制RabbitTemplate
    * */
    @PostConstruct   //MyRabbitConfig对象创建完成以后，执行这个方法
    public void initRabbitTemplate(){
        //设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData   当前消息的唯一关联数据（这个是消息的唯一id）
             * @param b                 消息是否成功收到
             * @param s                 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                
                System.out.println("confirm...correlationData: " +correlationData );
                System.out.println("ack: " +b +"----Cause: " + s  );

            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**w
             * 消息没有投递给指定的队列，才触发这个失败回调
             * @param message   投递失败的消息详细信息
             * @param i         回复的状态码
             * @param s         回复的文本内容
             * @param s1        当时这个消息发给哪个交换机
             * @param s2        当时这个消息发给哪个路由器
             */
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                System.out.println("Fail Message "+ message);
            }
        });
    }

}
