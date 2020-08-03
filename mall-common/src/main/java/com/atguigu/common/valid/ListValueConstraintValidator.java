package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

//<ListValue,Integer>： ListValue注解，此注解添加在Integer上
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {
    private Set<Integer> set = new HashSet<>();

    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        for(int val: vals){
            set.add(val);
        }
    }

    //判断是否校验成功
    // integer为注解所判断的实体变量的值
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {

        return set.contains(integer);
    }
}
