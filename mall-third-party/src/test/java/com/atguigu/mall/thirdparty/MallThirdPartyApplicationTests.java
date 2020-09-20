package com.atguigu.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

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

	public void sendSms(){
		String host = "https://smsmsgs.market.alicloudapi.com";  // 【1】请求地址 支持http 和 https 及 WEBSOCKET
		String path = "/sms/";  // 【2】后缀
		String appcode = "你自己的AppCode"; // 【3】开通服务后 买家中心-查看AppCode
		String code = "6578";  // 【4】请求参数，详见文档描述
		String phone = "18030939917";  //  【4】请求参数，详见文档描述
		String sign = "175622";   //  【4】请求参数，详见文档描述
		String skin = "1";  //  【4】请求参数，详见文档描述
		String urlSend = host + path + "?code=" + code +"&phone="+phone +"&sign="+sign +"&skin="+skin;   // 【5】拼接请求链接
		try {
			URL url = new URL(urlSend);
			HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
			httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE (中间是英文空格)
			int httpCode = httpURLCon.getResponseCode();
			if (httpCode == 200) {
				String json = read(httpURLCon.getInputStream());
				System.out.println("正常请求计费(其他均不计费)");
				System.out.println("获取返回的json:");
				System.out.print(json);
			} else {
				Map<String, List<String>> map = httpURLCon.getHeaderFields();
				String error = map.get("X-Ca-Error-Message").get(0);
				if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
					System.out.println("AppCode错误 ");
				} else if (httpCode == 400 && error.equals("Invalid Url")) {
					System.out.println("请求的 Method、Path 或者环境错误");
				} else if (httpCode == 400 && error.equals("Invalid Param Location")) {
					System.out.println("参数错误");
				} else if (httpCode == 403 && error.equals("Unauthorized")) {
					System.out.println("服务未被授权（或URL和Path不正确）");
				} else if (httpCode == 403 && error.equals("Quota Exhausted")) {
					System.out.println("套餐包次数用完 ");
				} else {
					System.out.println("参数名错误 或 其他错误");
					System.out.println(error);
				}
			}

		} catch (MalformedURLException e) {
			System.out.println("URL格式错误");
		} catch (UnknownHostException e) {
			System.out.println("URL地址错误");
		} catch (Exception e) {
			// 打开注释查看详细报错异常信息
			// e.printStackTrace();
		}
	}

	/*
	 * 读取返回结果
	 */
	private static String read(InputStream is) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
			line = new String(line.getBytes(), "utf-8");
			sb.append(line);
		}
		br.close();
		return sb.toString();
	}

}
