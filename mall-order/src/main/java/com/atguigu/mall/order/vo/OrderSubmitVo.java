package com.atguigu.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {
    private Long addrId;
    private Integer payType;
    //无需提交需要购买的商品，去购物车重新获取一遍

    //TODO invoice coupon

    private String orderToken;
    private BigDecimal payPrice;//TODO 验价
    private  String note;//TODO订单备注

    //用户相关信息在session中
}
