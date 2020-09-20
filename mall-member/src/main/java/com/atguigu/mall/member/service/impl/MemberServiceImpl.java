package com.atguigu.mall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.common.vo.SocialUser;
import com.atguigu.mall.member.dao.MemberLevelDao;
import com.atguigu.mall.member.entity.MemberLevelEntity;
import com.atguigu.mall.member.exception.PhoneExistException;
import com.atguigu.mall.member.exception.UsernameExistException;
import com.atguigu.mall.member.vo.MemberLoginVo;
import com.atguigu.mall.member.vo.MemberRegistVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.mall.member.dao.MemberDao;
import com.atguigu.mall.member.entity.MemberEntity;
import com.atguigu.mall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    //检测唯一性, 为了让controller能感知异常，使用异常机制
    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer mobile = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (mobile > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UsernameExistException {
        Integer username = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (username > 0) {
            throw new UsernameExistException();
        }

    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity entity = new MemberEntity();

        //检测唯一性, 为了让controller能感知异常，使用异常机制
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());

        //获取默认等级
        MemberLevelEntity level = memberLevelDao.getDefaultLevel();
        entity.setLevelId(level.getId());



        entity.setUsername(vo.getUserName());
        entity.setMobile(vo.getPhone());
        entity.setNickname(vo.getUserName());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);

//        passwordEncoder.matches()
        //TODO 其他需要得信息

        baseMapper.insert(entity);
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        //SELECT * FROM `ums_member` WHERE username=? OR mobile=?
        MemberEntity entity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if(entity==null){
            return null;
        }else {
            String password1 = entity.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            if(bCryptPasswordEncoder.matches(password,password1)){
                return entity;
            }
            return null;
        }
    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws IOException {
        //登录和注册合并逻辑
        //1. 通过数据库查询，判断当前社交用户是否第一次登陆（根据uid是否有相同）
        String uid = socialUser.getUid();
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        if(memberEntity !=null ){
            //用户已注册，更新token和token过期时间，并返回对象
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        }else {
            //2.没有查到当前社交用户对应的记录，进行注册
            MemberEntity register = new MemberEntity();

            try {
                //3. 查询当前社交用户的社交账号信息（昵称，性别等）
                Map<String, String> query = new HashMap<>();
                query.put("access_token", socialUser.getAccess_token());
                query.put("uid", socialUser.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
                if (response.getStatusLine().getStatusCode() == 200) {
                    //查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    register.setNickname(name);
                    register.setGender("m".equals(gender) ? 1 : 0);
                    //TODO 自行设置

                }
            }catch (Exception e){}
            //注册到数据库
            register.setSocialUid(socialUser.getUid());
            register.setAccessToken(socialUser.getAccess_token());
            register.setExpiresIn(socialUser.getExpires_in());
            baseMapper.insert(register);
            return register;
        }
    }

}