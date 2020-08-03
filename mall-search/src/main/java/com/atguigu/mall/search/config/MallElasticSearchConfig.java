package com.atguigu.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MallElasticSearchConfig {

    @Bean
    public RestHighLevelClient esRestClient(){
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("192.168.31.117",9200,"http")
        ));
        return client;
    }

    //请求设置项， 统一设置，为每次发送请求时都构件好请求头信息，因为ES为了安全需要这易操作，作单实例
    public static final RequestOptions COMMON_OPTIONS;
    static{
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }
}
