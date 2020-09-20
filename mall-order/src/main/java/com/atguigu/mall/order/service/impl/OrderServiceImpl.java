package com.atguigu.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.mall.order.constant.OrderConstant;
import com.atguigu.mall.order.entity.OrderItemEntity;
import com.atguigu.mall.order.feign.CartFeignService;
import com.atguigu.mall.order.feign.MemberFeignService;
import com.atguigu.mall.order.feign.WmsFeignService;
import com.atguigu.mall.order.interceptor.LoginUserInterceptor;
import com.atguigu.mall.order.to.OrderCreateTo;
import com.atguigu.mall.order.vo.*;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.mall.order.dao.OrderDao;
import com.atguigu.mall.order.entity.OrderEntity;
import com.atguigu.mall.order.service.OrderService;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import sun.misc.Request;

import javax.annotation.Resource;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    WmsFeignService wmsFeignService;
    @Resource
    StringRedisTemplate redisTemplate;

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo conformOrder() {

        OrderConfirmVo confirmVo = new OrderConfirmVo();

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();


        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            //1. Deliver Address   ums_member_receive_address
            //List<MemberAddressVo> address;
            RequestContextHolder.setRequestAttributes(requestAttributes);
            confirmVo.setAddress(memberFeignService.getAddress(memberRespVo.getId()));

        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //2. All selected items
            //List<OrderItemVo> items;
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            R skusHasStock = wmsFeignService.getSkusHasStock(collect);
            List<SkuStockVo> data = skusHasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null) {
                Map<Long, Boolean> collect1 = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(collect1);
            }
        }, executor);


        //TODO Invoice

        //3. Coupon
        //private Integer integration;
        confirmVo.setIntegration(memberRespVo.getIntegration());

        // purchase price Auto
        //BigDecimal total;
        // total price Auto
        //BigDecimal payPrice;

        //TODO   Avoid  multiple require
        //TODO String orderToken;
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);

        try {
//            CompletableFuture.allOf(getAddressFuture).get();
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(getAddressFuture, cartFuture);

            voidCompletableFuture.get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return confirmVo;
    }


    @Override
    public SubmitOrderResponse submitOrder(OrderSubmitVo vo) {

        SubmitOrderResponse response = new SubmitOrderResponse();

        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();

        confirmVoThreadLocal.set(vo);

        //1. 验证令牌【令牌的对比和删除保证原子性】
        // 脚本只返回0(令牌校验失败)和1(操作完全成功)
        String script = "if redis.call('get',KEY[1])==ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();

        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
//        String redisToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId());
//        if(orderToken!=null&&orderToken.equals(redisToken)){
//            //令牌验证通过
//            redisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId())
//
//        }
        if (result == 1L) {
            //令牌验证通过
            //TODO 下单去创建订单，验令牌，验库存，锁库存...

            OrderCreateTo order = createOrder();
        }

        //不通过
        return response;
    }

    private OrderCreateTo createOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();

        String orderSn = IdWorker.getTimeId();
        //1, order Number:
        OrderEntity orderEntity = buildOrder(orderSn);

        //3. obtain all order items
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);

        // verify the total price


        return orderCreateTo;

    }

    private OrderEntity buildOrder(String orderSn) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        //2.obtain shipping fee delivery adder
        OrderSubmitVo submitVo = confirmVoThreadLocal.get();
        R shippingFree = wmsFeignService.getShippingFree(submitVo.getAddrId());
        ShippingFeevo shippingRepvo = shippingFree.getData(new TypeReference<ShippingFeevo>() {
        });
        // shipping fee
        entity.setFreightAmount(shippingRepvo.getShippingFee());
        // receiver info
        entity.setReceiverCity(shippingRepvo.getAddress().getCity());
        entity.setReceiverDetailAddress(shippingRepvo.getAddress().getDetailAddress());
        entity.setReceiverName(shippingRepvo.getAddress().getName());
        entity.setReceiverPhone(shippingRepvo.getAddress().getPhone());
        entity.setReceiverPostCode(shippingRepvo.getAddress().getPostCode());
        entity.setReceiverProvince(shippingRepvo.getAddress().getProvince());
        entity.setReceiverRegion(shippingRepvo.getAddress().getRegion());
        return entity;
    }

    /**
     * For all order items
     *
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * build for each item
     *
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();

        // 1. order info: order ID
        // 2. Spu info
        Long skuId = cartItem.getSkuId();

        // 3. skuInfo
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());
        //TODO 4.coupon info

        // 5.accumulate points
        itemEntity.setGiftGrowth(cartItem.getPrice().intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().intValue());

        return null;
    }
}