package com.atguigu.mall.product.dao;

import com.atguigu.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 18:11:15
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
