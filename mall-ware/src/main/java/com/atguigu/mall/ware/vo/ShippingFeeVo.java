package com.atguigu.mall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShippingFeeVo {
    private MemberAddressVo address;
    private BigDecimal shippingFee;
}
