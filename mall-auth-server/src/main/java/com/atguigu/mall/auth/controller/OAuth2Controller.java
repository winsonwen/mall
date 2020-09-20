package com.atguigu.mall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.utils.R;
import com.atguigu.mall.auth.feign.MemberFeignService;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.mall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class OAuth2Controller {
    @Autowired
    MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    // http://auth.mall.com/oauth2.0/weibo/success?code=0236cede76844266e64ea8c4df7616b5
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {

        Map<String, String> map = new HashMap<>();

        //根据个人应用的信息填写，请求token，uuid数据
        map.put("client_id","2941903374");
        map.put("client_secret","2eacf86f6ee595345ca5a913b8d3619d");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.mall.com/oauth2.0/weibo/success");
        map.put("code",code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", null, null, map);

        //请求处理
        if(response.getStatusLine().getStatusCode() ==200){
            //success
            //获取返回的数据
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            //1. 当前用户如果是第一次进网站，就自动注册（为当前社交用户生成会员信息账号，以后这个账号就对应指定会员）
            //登录或者注册这个社交用户
            R r = memberFeignService.oauthLogin(socialUser);
            if(r.getCode()==0){
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {});
                log.info("Login Successfully {}",data.toString());

                session.setAttribute(AuthServerConstant.LOGIN_USER,data);
                //登录成功跳回首页
                return "redirect:http://mall.com";
            }else {

                return "redirect:http://auth.mall.com/login.html";
            }
        }else{
            return "redirect:http://auth.mall.com/login.html";

        }
    }

}
