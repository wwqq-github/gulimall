package com.atguigu.member.dao;

import com.atguigu.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-17 09:58:52
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
