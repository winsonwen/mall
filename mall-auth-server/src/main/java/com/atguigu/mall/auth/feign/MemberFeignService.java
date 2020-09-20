package com.atguigu.mall.auth.feign;

import com.atguigu.common.utils.R;
import com.atguigu.mall.auth.vo.SocialUser;
import com.atguigu.mall.auth.vo.UserLoginVo;
import com.atguigu.mall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-member")
public interface MemberFeignService {

    // member register
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
     R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
     R oauthLogin(@RequestBody SocialUser vo) throws Exception;
}
