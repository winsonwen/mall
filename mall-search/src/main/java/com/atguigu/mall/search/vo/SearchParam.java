package com.atguigu.mall.search.vo;

import lombok.Data;

import java.util.List;

/*
   封装页面所有可能传递过来的查询条件
    catalog3Id=225&keyword=xiaomi&sort=saleCount_asc&hasstock=0/1&brandId=1&brandId=2
    &attrs=1_other:android&attrs=2_5inch:6inch

*/
@Data
public class SearchParam {

    private String keyword; //全文匹配关键字

    private Long catalog3Id; //三级分类id

    private String _queryString;  // param in url
    /*
    * saleCount:  sort=saleCount_asc/desc
    * hostScore:  sort=hostScore_asc/desc
    * skuPrice:   sort=skuPrice_asc/desc
    * */
    private String sort; //排序条件

    /*
    * hasStock, skuPrice, brandId, catalog3Id, attrs
    *   hasStock=0/1
    *   skuPrice=1_500/_500/500_
    *   brandId=1
    * */
    private Integer hasStock; //是否只显示有货
    private String skuPrice;
    private List<Long> brandId; //按照品牌查询，多选
    private List<String> attrs;  //按照属性筛选
    private Integer pageNum=1;    //页码

}
