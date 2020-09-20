package com.atguigu.mall.cart.service;

import com.atguigu.mall.cart.vo.Cart;
import com.atguigu.mall.cart.vo.CartItem;

import java.util.List;

public interface CartService {

    //将商品添加到购物车
    CartItem addToCart(Long skuId, Integer num);
    //获取购物车中某个购物项
    CartItem getCartItem(Long skuId);

    Cart getCart();

    //clear data in cart
    void clearCart(String cartKey);

    /**
     * check item
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, Integer check);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItem> getUserCartCheckedItems();
}
