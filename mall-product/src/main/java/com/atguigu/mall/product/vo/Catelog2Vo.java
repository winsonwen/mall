package com.atguigu.mall.product.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 耳机分类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catelog2Vo {

    private String catalogId;

    private List<Catelog3Vo> catalog3List;

    private String id;

    private String name;


    //三级分类
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catelog3Vo {

        private String catalog2Id;

        private String id;

        private String name;

    }

}
