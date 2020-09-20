package com.atguigu.mall.product;

//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClient;
//import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.mall.product.config.MyRedissonConfig;
import com.atguigu.mall.product.dao.AttrGroupDao;
import com.atguigu.mall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.mall.product.entity.BrandEntity;
import com.atguigu.mall.product.service.BrandService;
import com.atguigu.mall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.mall.product.vo.SkuItemVo;
import com.atguigu.mall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jdk.internal.util.xml.impl.Input;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class MallProductApplicationTests {
//	@Autowired
//	BrandService brandService;
//	@Autowired
//	StringRedisTemplate stringRedisTemplate;
//	@Autowired
//	RedissonClient redissonClient;

	@Autowired
	AttrGroupDao attrGroupDao;
	@Autowired
	SkuSaleAttrValueDao skuSaleAttrValueDao;
	@Test
	public void test(){
//		List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(13L, 225L);
//		System.out.println("result:" + attrGroupWithAttrsBySpuId.toString());
		List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(13L);
		System.out.println("result: " + saleAttrsBySpuId);
	}




//	@Test
//	public void testRedisson(){
//		System.out.println(redissonClient);
//
//	}





//	@Test
//	public void testStringRedisTemplate(){
//		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
//		// save
//		ops.set("hello","world_"+ UUID.randomUUID().toString());
//		 // query
//		String hello = ops.get("hello");
//		System.out.println(hello);
//	}


//	@Autowired
//	OSSClient ossClient;



	@Test
	public void testUpload() throws FileNotFoundException {
//		// Endpoint以杭州为例，其它Region请按实际情况填写。
//		String endpoint = "oss-cn-shanghai.aliyuncs.com";
//		// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//		String accessKeyId = "LTAI4Fz7P8aB6BijkJf1Hqsq";
//		String accessKeySecret = "2l9z8e1X8Qicq8TPapWBVsVwF4Z8XV";
//		// 创建OSSClient实例。
//		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//		// 上传文件流。
////		InputStream inputStream = new FileInputStream("<yourlocalFile>");
////		ossClient.putObject("<yourBucketName>", "<yourObjectName>", inputStream);
//		InputStream inputStream = new FileInputStream("Z:\\tool\\guli资料\\pics\\0d40c24b264aa511.jpg");
//		ossClient.putObject("mall-wen", "0d40c24b264aa511.jpg", inputStream);
//		// 关闭OSSClient。
//		ossClient.shutdown();
//		System.out.println("Updated Successfully");

//		InputStream inputStream = new FileInputStream("Z:\\tool\\guli资料\\pics\\1f15cdbcf9e1273c.jpg");
//		ossClient.putObject("mall-wen", "1f15cdbcf9e1273c.jpg", inputStream);
//		ossClient.shutdown();
//		System.out.println("Updated Successfully");
	}


//	@Test
//	void contextLoads() {
////		BrandEntity brandEntity = new BrandEntity();
////		brandEntity.setName("Huawei");
////		brandService.save(brandEntity);
////		System.out.println("Save Successfully...");
//
////		BrandEntity brandEntity = new BrandEntity();
////		brandEntity.setBrandId(1L);
////		brandEntity.setDescript("Google");
////		brandService.updateById(brandEntity);
//
//		List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
//		list.forEach((item)->
//				System.out.println(item)
//		);
//	}
}
