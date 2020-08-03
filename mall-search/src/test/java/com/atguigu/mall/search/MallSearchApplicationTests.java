package com.atguigu.mall.search;

import com.alibaba.fastjson.JSON;
import com.atguigu.mall.search.config.MallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;

import java.io.IOException;

@SpringBootTest
class MallSearchApplicationTests {
	@Autowired
	private RestHighLevelClient client;

	/*
	* test: Store or Update Data to ES
	* */
	@Test
	void indexData() throws IOException {
		// users: index name
		IndexRequest indexRequest = new IndexRequest("users");
		indexRequest.id("1");
//		indexRequest.source("userName","zhangsan", "age", 18, "gender","male");

		User user = new User();
		user.setAge(18);
		user.setUserName("zhangsan");
		user.setGender("male");
		String jsonString = JSON.toJSONString(user);
		indexRequest.source(jsonString, XContentType.JSON);

		//Execute Save Action
		IndexResponse index = client.index(indexRequest, MallElasticSearchConfig.COMMON_OPTIONS);

		//Responding
		System.out.println(index);
	}

	@Data
	class User{
		private String userName;
		private String gender;
		private Integer age;
	}


	// 测试查询数据
	@Test
	void searchData2() throws IOException {
//        GET /bank/_search
//        {
//            "query": {
//            "term": {
//                "address": {
//                    "value": "mill"
//                }
//            }
//        },
//            "aggs": {
//            "aggAvg": {
//                "avg": {
//                    "field":"age"
//                }
//            },
//            "balanceAgg":{
//                "terms": {
//                    "field": "balance",
//                            "size": 10
//                }
//            }
//        }
//        }
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("bank");

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.termQuery("address", "mill"));

		// 按照薪资的值进行分布
		TermsAggregationBuilder balance = AggregationBuilders.terms("balanceAgg")
				.field("balance");
		searchSourceBuilder.aggregation(balance);

		// 计算平均年龄
		AvgAggregationBuilder age = AggregationBuilders.avg("ageAvg").field("age");
		searchSourceBuilder.aggregation(age);

		searchRequest.source(searchSourceBuilder);
		System.out.println(searchSourceBuilder.toString());

		SearchResponse search = client.search(searchRequest, MallElasticSearchConfig.COMMON_OPTIONS);

		System.out.println(search.toString());

		// 得到hits部分
		SearchHits hits = search.getHits();
		//获取命中的记录
		SearchHit[] data = hits.getHits();
		for (SearchHit datum : data) {
			String sourceAsString = datum.getSourceAsString();
			//根据返回的数据的对象先创建一个class
//			ResultData resultData = JSON.parseObject(sourceAsString, ResultData.class);
//			System.out.println(resultData);
		}

		Aggregations aggregations = search.getAggregations();
//        List<Aggregation> aggregationsData = aggregations.asList();
//        for (Aggregation aggregationsDatum : aggregationsData) {
//            String name = aggregationsDatum.getName();
//            System.out.println(name);
//        }
		Terms balanceAgg = aggregations.get("balanceAgg");
		for (Terms.Bucket bucket : balanceAgg.getBuckets()) {
			String keyAsString = bucket.getKeyAsString();
			System.out.println("薪资" + keyAsString);
		}

		Avg aggAvg = aggregations.get("ageAvg");
		double value = aggAvg.getValue();
		System.out.println("平均年龄" + value);

	}



	@Test
	void contextLoads() {
		System.out.println(client);
	}

}
