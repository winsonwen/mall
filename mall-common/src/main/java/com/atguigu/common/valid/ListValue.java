package com.atguigu.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import java.lang.annotation.*;


@Documented
//使用哪个校验器
@Constraint(validatedBy = {ListValueConstraintValidator.class})
//注解可标注的位置
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
//注解运行时机，运行时可以获取
@Retention(RetentionPolicy.RUNTIME)
//@Repeatable(NotEmpty.List.class)  可重复注解
public @interface ListValue {

    //校验出错的时候信息去哪
    String message() default "{com.atguigu.common.valid.ListValue.message}";
    //要支持分组校验
    Class<?>[] groups() default {};
    //自定义负载信息
    Class<? extends Payload>[] payload() default {};

    int[] vals() default {};
}
