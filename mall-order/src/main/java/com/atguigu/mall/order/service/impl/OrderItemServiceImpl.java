package com.atguigu.mall.order.service.impl;

import com.atguigu.mall.order.entity.OrderReturnReasonEntity;
import com.atguigu.mall.order.service.OrderReturnApplyService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.mall.order.dao.OrderItemDao;
import com.atguigu.mall.order.entity.OrderItemEntity;
import com.atguigu.mall.order.service.OrderItemService;

import javax.xml.transform.Templates;


/*
* @RabbitListener 可以加载类+方法上  （用于监听哪些队列）
* @RabbitHandler  只能加载方法上    （用于重载区分不同的消息）
*
* */
@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /*
    * queues: declaring for monitoring queues
    *
    * 参数可以写以下类型：
    * 1. message: 原生消息详细信息。 头+体
    * 2。 T<发送消息的类型> OrderReturnReasonEntity content：这里会直接转换成content对象，无需别的操作
    * 3. Channel channel：当前传输数据的通道
    *
    * Queue可以多人都来监听，只要收到消息，队列就会删除消息，而且只能有一个收到此消息
    * 场景：
    *   1） 订单服务启动多个；同一个消息只能有一个客户端收到
    *   2） 只有一个消息完全处理完，再接收下一个消息
    * */
    //
//    @RabbitListener(queues = {"hello-java-queue"})
    @RabbitHandler
    public void receiveMessage(Message message, OrderReturnReasonEntity content, Channel channel){
        byte[] body = message.getBody();

        //channel内按顺序自增的数据
        long deliveryTag = message.getMessageProperties().getDeliveryTag();


        try {
            if (deliveryTag % 2 == 0) {
                //确定接收数据包
                //回复ack,false代表非批量ack模式
                channel.basicAck(deliveryTag,false);
            }else {
                //basicNack可以批量处理，第二个参数，basicReject则无法批量
                //拒收数据包,第三个参数为false，即直接丢弃数据，为true则重新发回queue中
                channel.basicNack(deliveryTag, false,false);
                //拒收数据包,第二个参数为false，即直接丢弃数据，为true则重新发回queue中
                channel.basicReject(deliveryTag,false);
            }
        } catch (IOException e) {
            //网络中断
            e.printStackTrace();
        }
    }


}