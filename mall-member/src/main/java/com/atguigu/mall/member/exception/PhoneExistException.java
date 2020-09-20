package com.atguigu.mall.member.exception;

import org.omg.SendingContext.RunTime;

public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号已存在");
    }
}
