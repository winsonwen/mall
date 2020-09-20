package com.atguigu.mall.product.vo;

import com.atguigu.mall.product.entity.SkuImagesEntity;
import com.atguigu.mall.product.entity.SkuInfoEntity;
import com.atguigu.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class SkuItemVo {

    //1. obtain sku basic information (pms_sku_info)
    SkuInfoEntity info;
    boolean hasStock=true;

    //2. sku's images   (pms_sku_images)
    List<SkuImagesEntity> images;

    //3. sku's product attributes
    List<SkuItemSaleAttrVo> saleAttr;

    //4. spu's information
    SpuInfoDescEntity desc;

    //5. spu's Specification
    List<SpuItemAttrGroupVo> groupAttrs;




}
