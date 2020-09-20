package com.atguigu.mall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegistVo {
    @NotEmpty(message = "user name should be submitted")
    @Length(min=6, max=18,message = "user name should be 6-18 character")
    private String userName;

    @NotEmpty(message = "Password should be submit")
    @Length(min=6, max=18,message = "password should be 6-18 character")
    private String password;

    @NotEmpty(message = "Phone number should be submitted")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "The format of phone Number isn't correct")
    private String phone;

    @NotEmpty(message = "Verification code should be submitted")
    private String code;

}
