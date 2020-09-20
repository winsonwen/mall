package com.atguigu.mall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class MallFeignConfig {
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor(){

            @Override
            public void apply(RequestTemplate requestTemplate) {
                //RequestContextHolder 拿到刚进来的调用的这个请求
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if(requestAttributes!=null) {
                    HttpServletRequest request = requestAttributes.getRequest();//老请求
                    if (request != null) {
                        //同步请求头数据，cookie
                        requestTemplate.header("Cookie", request.getHeader("Cookie"));
                    }
                }
            }
        };
    }
}
