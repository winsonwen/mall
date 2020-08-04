package com.atguigu.mall.product.exception;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/*
    集中处理所有异常
*/
@Slf4j
//@RestController
//@ControllerAdvice(basePackages = "com.atguigu.mall.product.controller")
@RestControllerAdvice(basePackages = "com.atguigu.mall.product.controller")
public class MallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}, 异常类型：{}", e.getMessage(), e.getCause());
        BindingResult result = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        result.getFieldErrors().forEach((item)->{
//                // 获取到错误提示
//                String message = item.getDefaultMessage();
//                // 获取错误的属性的名字
//                String field=item.getField();
                errorMap.put(item.getField(), item.getDefaultMessage());
        });

        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(), BizCodeEnume.VALID_EXCEPTION.getMsg()).put("data",errorMap);
    }

    //处理所有异常
    @ExceptionHandler(Throwable.class)
    public R handleException(Throwable throwable){
        System.out.println(throwable.toString());
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
