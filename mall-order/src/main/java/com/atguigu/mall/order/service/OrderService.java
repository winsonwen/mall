package com.atguigu.mall.order.service;

import com.atguigu.mall.order.vo.OrderConfirmVo;
import com.atguigu.mall.order.vo.OrderSubmitVo;
import com.atguigu.mall.order.vo.SubmitOrderResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.mall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 
 *
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 21:41:29
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页返回需要用的数据
     * @return
     */
    OrderConfirmVo conformOrder();

    /**
     * 下单操作
     * @param vo
     * @return
     */
    SubmitOrderResponse submitOrder(OrderSubmitVo vo);
}

