package com.atguigu.mall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

//data using in checkout page
@ToString
public class OrderConfirmVo {

    //Deliver Address   ums_member_receive_address
    @Setter @Getter
    List<MemberAddressVo> address;

    //All selected items
    @Setter @Getter
    List<OrderItemVo> items;

    //TODO Invoice

    //Coupon
    @Setter @Getter
    private Integer integration;

    // Avoid  multiple require
    @Setter @Getter
    String orderToken;
    @Setter @Getter
    Map<Long, Boolean> stocks;

    // purchase price
    BigDecimal total;
    // total price
    BigDecimal payPrice;



    public Integer getCount(){
        Integer i=0;
        if (items != null) {
            for (OrderItemVo item : items) {
                i +=item.getCount();
            }
        }
        return i;
    }

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        //TODO total - discount
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }
}
