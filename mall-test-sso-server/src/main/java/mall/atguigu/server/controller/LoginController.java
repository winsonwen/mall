package mall.atguigu.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("token") String token){
        String s = redisTemplate.opsForValue().get(token);
        return s;
    }

    //@CookieValue(   浏览器传送过来的cookie信息
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String url, Model model,
                            @CookieValue(value="sso_token",required = false)String sso_token){

        if(!StringUtils.isEmpty(sso_token)){
            //之前有人登录过，浏览器留下了痕迹
            return "redirect:" + url+"?token="+sso_token;
        }

        model.addAttribute("url",url);
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,@RequestParam("password") String password, @RequestParam("url") String url
            , HttpServletResponse response){
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            //登录成功，跳回到之前的页面
            //假设吧登录成功的用户存起来
            String uuid= UUID.randomUUID().toString().replace("-","");
            redisTemplate.opsForValue().set(uuid,username);

            Cookie sso_token = new Cookie("sso_token",uuid);
            response.addCookie(sso_token);

            return "redirect:" + url+"?token="+uuid;
        }else {
            return "login";
        }
    }

}
