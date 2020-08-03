package com.atguigu.mall.member.dao;

import com.atguigu.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author winson
 * @email winsonwen4@gmail.com
 * @date 2020-07-20 21:27:10
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
