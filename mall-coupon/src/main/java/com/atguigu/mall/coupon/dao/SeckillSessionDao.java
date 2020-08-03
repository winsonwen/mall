package com.atguigu.mall.coupon.dao;

import com.atguigu.mall.coupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动场次
 * 
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 21:11:58
 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {
	
}
