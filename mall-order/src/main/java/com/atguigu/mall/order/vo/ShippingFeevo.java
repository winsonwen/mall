package com.atguigu.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShippingFeevo {

    private MemberAddressVo address;
    private BigDecimal shippingFee;
}
