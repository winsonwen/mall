package com.atguigu.mall.order;

import com.atguigu.mall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@Slf4j
@SpringBootTest
class MallOrderApplicationTests {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	public void sendMessageTest(){
		// if sending message is a object, it will be serialized. need to implement Serializable to send by Json data
		OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
		entity.setId(1L);
		entity.setCreateTime(new Date());
		entity.setName("666");
		//1. send Message
//		String s = "Hello World";
//		rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java",s);
		rabbitTemplate.convertAndSend("hello-java-exchange", "hello-java-exchange",entity);

		log.info("send successfully{}", entity );
	}

	/**
	 * 1. Create Exchange[hello-java-exchange], Queue, Binding
	 * 		1) using AmqpAdmin
	 * 2. send and receive message
	 * 		1) using RabbitTemplate
	 */
	@Autowired
	AmqpAdmin amqpAdmin;

	@Test
	void declareExchange() {
		//    public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
		DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
		amqpAdmin.declareExchange(directExchange);
		log.info("Create successfully" + directExchange.getName());
	}

	@Test
	void declareQueue() {
		//    public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments) {
		//exclusive: only one connection
		Queue queue = new Queue("hello-java-queue",true,false,false);
		amqpAdmin.declareQueue(queue);
		log.info("Create successfully" + queue.getName());
	}

	@Test
	void declareBinding() {
		//     public Binding(String destination, Binding.DestinationType【queue or exchange】 destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments) {
		Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange", "hello.java", null);
		amqpAdmin.declareBinding(binding);

	}



}
