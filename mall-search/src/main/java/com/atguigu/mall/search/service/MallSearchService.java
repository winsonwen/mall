package com.atguigu.mall.search.service;

import com.atguigu.mall.search.vo.SearchParam;
import com.atguigu.mall.search.vo.SearchResult;

public interface MallSearchService {

    /**
     *
     * @param param 检索的参数
     * @return      返回检索的结果
     */
    SearchResult search(SearchParam param);
}
