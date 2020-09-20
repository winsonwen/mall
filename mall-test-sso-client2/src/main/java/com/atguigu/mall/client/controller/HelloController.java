package com.atguigu.mall.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {

    @Value("${sso.server.url}")
    String ssoServerUrl;



    //无需登录就可访问
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }


    @GetMapping("/boss")
    public String employees(Model model, HttpSession session, @RequestParam(value = "token", required = false)String token) {

        //这个部分可以写成一个方法，利用token去服务器取得对象信息
        if(!StringUtils.isEmpty(token)){
            //去ssoserver登录成功跳回来会戴上token
            // 需要去ssoserver获取当前token真正对应的用户信息
            // 不应该用openfeign拿来调用，因为服务器可能拿php写的
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhostL8080/userInfo?token" + token, String.class);

            String body = forEntity.getBody();

            session.setAttribute("loginUser",body);
        }

        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            //没登陆，跳转到登录服务器进行登录，使用url上的查询参数标识自己是哪个页面
            return "redirect:"+ssoServerUrl+"?redirect_url=http://localhost:8082/boss";
        } else {

            List<String> emps = new ArrayList<>();
            emps.add("Jack");
            emps.add("Jone");
            model.addAttribute("emps", emps);
            return "list";

        }
    }

}
