package com.atguigu.mall.product;

import com.atguigu.mall.product.dao.AttrAttrgroupRelationDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.atguigu.mall.product.dao")   //扫描Mapper
@SpringBootApplication
public class MallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(MallProductApplication.class, args);
	}

}
