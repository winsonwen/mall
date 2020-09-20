package com.atguigu.mall.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class MallSessionConfig {

    //解决子域session共享问题
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("mall.com");
        cookieSerializer.setCookieName("MALLSESSION");

        return cookieSerializer;
    }

    //使用JSON的序列化方式来序列化对象数据到redis中（方便读取与存储）
    public RedisSerializer<Object> redisSerializer(){

        return new GenericJackson2JsonRedisSerializer();

    }


}
