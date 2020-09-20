package com.atguigu.mall.cart.vo;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class UserInfoTo {

        private Long userId;
        private String userKey;
        private boolean temUser=false; //cookie中是否有临时用户

}
