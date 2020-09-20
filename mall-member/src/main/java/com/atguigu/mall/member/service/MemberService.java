package com.atguigu.mall.member.service;

import com.atguigu.common.vo.SocialUser;
import com.atguigu.mall.member.exception.PhoneExistException;
import com.atguigu.mall.member.exception.UsernameExistException;
import com.atguigu.mall.member.vo.MemberLoginVo;
import com.atguigu.mall.member.vo.MemberRegistVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.mall.member.entity.MemberEntity;

import java.io.IOException;
import java.util.Map;

/**
 * 会员
 *
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 21:27:10
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //检测唯一性, 为了让controller能感知异常，使用异常机制
    void checkPhoneUnique(String phone) throws PhoneExistException;
    void checkUserNameUnique(String userName) throws UsernameExistException;

    void regist(MemberRegistVo vo);

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser) throws IOException;
}

