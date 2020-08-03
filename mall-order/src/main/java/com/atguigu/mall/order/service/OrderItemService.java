package com.atguigu.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.mall.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 
 *
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 21:41:29
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

