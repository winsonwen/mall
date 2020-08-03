package com.atguigu.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class MallThirdPartyApplicationTests {

	@Autowired
	OSSClient ossClient;

	@Test
	void contextLoads() throws FileNotFoundException {
		InputStream inputStream = new FileInputStream("Z:\\tool\\guli资料\\pics\\1f15cdbcf9e1273c.jpg");
		ossClient.putObject("mall-wen", "1f15cdbcf9e1273c.jpg", inputStream);
		ossClient.shutdown();
		System.out.println("Updated Successfully");
	}

}
