package com.atguigu.mall.product.dao;

import com.atguigu.mall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 18:11:15
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateCategory( Long catId, String name);

}
