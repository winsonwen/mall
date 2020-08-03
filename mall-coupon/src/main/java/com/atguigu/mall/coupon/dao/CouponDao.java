package com.atguigu.mall.coupon.dao;

import com.atguigu.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 21:11:59
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
