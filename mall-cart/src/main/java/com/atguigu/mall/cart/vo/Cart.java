package com.atguigu.mall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

//cart for user
// override get method for calculate
public class Cart {
    List<CartItem> items;

    //number of item
    private Integer countNum;
    //number of item categories
    private Integer countType;

    private BigDecimal totalAmount;

    private BigDecimal discount= new BigDecimal("0");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int countNum=0;
        if(items!=null&&items.size()>0) {
            for (CartItem item : items) {
                countNum+=item.getCount();
            }
        }
        return countNum;
    }

    public Integer getCountType() {
        int count=0;
        if(items!=null&&items.size()>0) {
            for (CartItem item : items) {
                count+=1;
            }
        }
        return count;

    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        int count=0;
        if(items!=null&&items.size()>0) {
            for (CartItem item : items) {
                if(item.getCheck()) {
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        amount = amount.subtract(this.getDiscount());
        return amount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }


}
