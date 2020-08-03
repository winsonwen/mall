package com.atguigu.mall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.atguigu.mall.member.feign")
@EnableDiscoveryClient   //For Nacos Discovery
@SpringBootApplication
public class MallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallMemberApplication.class, args);
	}

}
