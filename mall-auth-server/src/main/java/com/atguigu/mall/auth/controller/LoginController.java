package com.atguigu.mall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.DomainName;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.mall.auth.vo.UserLoginVo;
import com.atguigu.mall.auth.vo.UserRegistVo;
import com.atguigu.mall.auth.feign.MemberFeignService;
import com.atguigu.mall.auth.feign.ThirdPartFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
    @Autowired
    ThirdPartFeignService thirdPartFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    MemberFeignService memberFeignService;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone) {

        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        String sentCode= UUID.randomUUID().toString().substring(0, 5);
        String code = sentCode + "_" + System.currentTimeMillis();

        //TODO 1. 接口防刷

        //2.验证码再次校验。Redis  save key=phone, value=code sms:code:phonenum - code
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code, 10, TimeUnit.MINUTES);
        //防止同一个手机在再60秒内再次发送验证码

        thirdPartFeignService.sendCode(phone, sentCode);
        return R.ok();
    }

    //TODO 重定向携带数据，利用session原理，将数据放在session中，只要跳到下一个页面取出这个数据后，里面的数据就会删掉
    //TODO 1. 会出现分布式下得session问题
    //RedirectAttributes redirectAttributes: 模拟重定向携带数据
    @PostMapping("/regist")
    public String register(@Valid UserRegistVo vo, BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> error = new HashMap<>();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                if (!error.containsKey(fieldError.getField())) {
                    error.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
            }

            //Duplicate key
//            Map<String,String> error = result.getFieldErrors().stream().collect(Collectors.toMap(fieldError -> {
//                return fieldError.getField();
//            },fieldError -> {return fieldError.getDefaultMessage();}));
//            Map<String,String> error = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));


            redirectAttributes.addFlashAttribute("errors", error);

            // Request method 'POST' not supported
            //用户注册 -> /regist[post] --> 转发/reg.html(路径映射默认都是get方式访问的)
//            return "forward:/reg.html";

            return "redirect:http://auth.mall.com/reg.html";
        }

        //1. 校验验证码
        String code = vo.getCode();
        String key =AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone();
        String s = redisTemplate.opsForValue().get(key);

        if (!StringUtils.isEmpty(s)) {

            if (code.equals(s.split("_")[0])) {
                //删除验证码
                Boolean delete = redisTemplate.delete(key);
                //验证码通过   //真正注册，调用远程服务进行注册
                R r = memberFeignService.regist(vo);

                if(r.getCode()==0){
                    //成功
                    return "redirect:http://auth.mall.com/login.html";
                }else {
                    //返回错误信息
                    Map<String, String> error = new HashMap<>();
                    error.put("msg", r.getData("msg", new TypeReference<String>(){}));

                    redirectAttributes.addFlashAttribute("errors", error);
                    return "redirect:http://auth.mall.com/reg.html";
                }

            }
        }
        Map<String, String> error = new HashMap<>();
        error.put("code", "验证码错误");
        redirectAttributes.addFlashAttribute("errors", error);
        return "redirect:http://auth.mall.com/reg.html";

    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute==null){
            //没登录
            return "login";
        }else {
            //login already, redirect to default page
            return  "redirect:"+ DomainName.DEFAULT_PAGE;
        }


    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes,
                        HttpSession session){

        R login = memberFeignService.login(vo);

        if(login.getCode()==0){
            //success
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>(){});
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            return "redirect:http://mall.com";
        }else {
            Map<String, String> error = new HashMap<>();
            error.put("msg", login.getData("msg", new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors", error);

            return "redirect:http://auth.mall.com/login.html";
        }
    }

}
