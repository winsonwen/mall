package com.atguigu.mall.order.vo;

import com.atguigu.mall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponse {
    private OrderEntity order;
    private Integer code; // 0 表示成功

}
