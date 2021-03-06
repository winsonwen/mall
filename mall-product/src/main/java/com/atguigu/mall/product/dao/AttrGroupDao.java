package com.atguigu.mall.product.dao;

import com.atguigu.mall.product.entity.AttrGroupEntity;
import com.atguigu.mall.product.vo.SkuItemVo;
import com.atguigu.mall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 18:11:16
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
