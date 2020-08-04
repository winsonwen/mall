package com.atguigu.mall.product.vo;
import lombok.Data;

/**
 * @AUTHOR: raymond
 * @DATETIME: 2020/5/4  17:48
 * DESCRIPTION:
 **/
@Data
public class AttrResponseVO extends AttrVo {

    private String catelogName;

    private String groupName;

    private Long[] catelogPath;

}
