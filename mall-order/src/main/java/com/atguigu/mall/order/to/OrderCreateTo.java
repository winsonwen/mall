package com.atguigu.mall.order.to;

import com.atguigu.mall.order.entity.OrderEntity;
import com.atguigu.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {
    private OrderEntity order;

    private List<OrderItemEntity> orderItems;
    private List<OrderItemEntity> orderItem;
    private BigDecimal payPrice;    //calculate payed price
    private BigDecimal shippingFee;


}
