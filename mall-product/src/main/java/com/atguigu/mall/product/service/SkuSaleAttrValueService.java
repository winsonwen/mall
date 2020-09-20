package com.atguigu.mall.product.service;

import com.atguigu.mall.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.mall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 18:11:15
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {


    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(Long spuId);

    PageUtils queryPage(Map<String, Object> params);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

