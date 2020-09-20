package com.atguigu.mall.order.web;

import com.atguigu.mall.order.service.OrderService;
import com.atguigu.mall.order.vo.OrderConfirmVo;
import com.atguigu.mall.order.vo.OrderSubmitVo;
import com.atguigu.mall.order.vo.SubmitOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model){

        OrderConfirmVo confirmVo =orderService.conformOrder();
        model.addAttribute("orderConfirmData",confirmVo);
        //Checkout page
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo){

         SubmitOrderResponse response = orderService.submitOrder(vo);

        //下单失败回到订单确认页重新确认订单信息
        System.out.println("----" + vo);
        if(response.getCode()==0){
            //下单成功来到支付选择页
            return "pay";
        }else {
            return "redirect:http://order.mall.com/toTrade";
        }

    }
}
