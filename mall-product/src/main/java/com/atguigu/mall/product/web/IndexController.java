package com.atguigu.mall.product.web;

import com.atguigu.mall.product.entity.CategoryEntity;
import com.atguigu.mall.product.feign.CouponFeignService;
import com.atguigu.mall.product.service.CategoryService;
import com.atguigu.mall.product.vo.Catelog2Vo;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedissonClient redisson;

    @GetMapping({"/", "/index.html", "/index"})
    public String indexPage(Model model) {
        //TODO 1.查出所有一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        //交给页面的返回值，返回值会放到页面的请求域中
        model.addAttribute("categories", categoryEntities);

        //试图解析器进行品串：
        //default prefix：classpath:/templates
        //default postfix：.html
        return "index";
    }

    // index/catalog.json
    @GetMapping("/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        Map<String, List<Catelog2Vo>> catelogJson = categoryService.getCatelogJson();
        return catelogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        // 可重用锁
//        //1. 获取一把锁，只要锁名一样，就是同一把锁
//        RLock lock = redisson.getLock("my-lock");
//
//        //2. 加锁
//        lock.lock();
//
//        //业务规定，无论运不运行成功，都应进行解锁工作
//        try {
//            System.out.println("Lock successfully"+Thread.currentThread().getId());
//        } catch (Exception e) {
//        } finally {
//            //3. 解锁
//            System.out.println("UnLock successfully"+Thread.currentThread().getId());
//            lock.unlock();
//        }
        //-----------------------------------------------
        //读写锁

        //闭锁

        return "hello";
    }


}
