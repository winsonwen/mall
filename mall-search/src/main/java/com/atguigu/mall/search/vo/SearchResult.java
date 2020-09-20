package com.atguigu.mall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchResult {
    //查询到的所有商品信息
    private List<SkuEsModel> product;

    private Integer pageNum; //当前页码
    private Long total; //总记录数
    private Integer totalPages; //总页数

    private List<BrankVo> brands; //当前查询到的结果所有涉及到的品牌
    private List<CatalogVo> catalogs; //当前查询到的结果所有分类
    private List<AttrVo> attrs;  //当前查询到的结果所有属性

    private List<Integer> pageNavs;

    // BreadcrumbNavigation
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrankVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;

    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
