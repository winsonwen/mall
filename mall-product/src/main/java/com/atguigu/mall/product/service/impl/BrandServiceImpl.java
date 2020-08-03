package com.atguigu.mall.product.service.impl;

import com.atguigu.mall.product.controller.CategoryBrandRelationController;
import com.atguigu.mall.product.service.CategoryBrandRelationService;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.mall.product.dao.BrandDao;
import com.atguigu.mall.product.entity.BrandEntity;
import com.atguigu.mall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String  key = (String) params.get("key");

        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();

        if(!StringUtils.isNullOrEmpty(key)){
            //设置sql查询
            wrapper.eq("brand_id",key).or().like("name",key);
        }
        //检索所有
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params), wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateDetail(BrandEntity brand) {
        //保证冗余字段的数据一致
        this.updateById(brand);
        if(StringUtils.isNullOrEmpty(brand.getName())){
            //同步更新其他关联表中的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName() );
             //TODO 更新其他关联
        }

    }

}