package com.atguigu.mall.ware.dao;

import com.atguigu.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 21:46:31
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
