package com.atguigu.mall.product.service.impl;

import com.atguigu.mall.product.vo.AttrGroupRelationVo;
import com.mysql.cj.util.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.mall.product.dao.AttrGroupDao;
import com.atguigu.mall.product.entity.AttrGroupEntity;
import com.atguigu.mall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        //new Query<AttrGroupEntity>().getPage(params)   提取获得从前端提交的Map的值
        //new QueryWrapper<AttrGroupEntity>()  所需要查的表的实体类
//        System.out.println("1111111111111:"+ params.toString());
//        System.out.println("222222222222:"+ catelogId);

        //检索条件
        String key =(String) params.get("key");
        //select * from pms_arrt_group where catelog id=? and (attr_group_id=key or attr_group_name like %key%)
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        if(!StringUtils.isNullOrEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }

        //如果不是3級分類
        if(catelogId==0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper );
            return new PageUtils(page);
        }else{
            wrapper.eq("catelog_id",catelogId);

            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);

            return new PageUtils(page);
        }

    }



}