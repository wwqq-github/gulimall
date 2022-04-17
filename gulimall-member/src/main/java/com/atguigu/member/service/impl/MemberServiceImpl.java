package com.atguigu.member.service.impl;

import com.atguigu.common.vo.MemberVO;
import com.atguigu.member.exception.LoginException;
import com.atguigu.member.exception.MemberException;
import com.atguigu.member.service.MemberLevelService;
import com.atguigu.member.vo.LoginVo;
import com.atguigu.member.vo.RegisteVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.member.dao.MemberDao;
import com.atguigu.member.entity.MemberEntity;
import com.atguigu.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void registerMember(RegisteVO user) {
        //判断用户名是否存在
        checkUsername(user.getUserName());
        //判断手机号是否存在
        checkPhone(user.getPhone());
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setCreateTime(new Date());
        memberEntity.setUsername(user.getUserName());
        memberEntity.setMobile(user.getPhone());
        memberEntity.setLevelId(memberLevelService.getDefaultLevel());
        String changedPswd = DigestUtils.md5Hex(user.getPhone() + user.getPassword());
        memberEntity.setPassword(changedPswd);
        this.save(memberEntity);

    }

    @Override
    public MemberEntity loginMember(LoginVo memberVo) {
        QueryWrapper<MemberEntity> eq = new QueryWrapper<MemberEntity>().eq("username", memberVo.getLoginacct()).or().eq("mobile", memberVo.getLoginacct()).or().eq("email", memberVo.getLoginacct());
        List<MemberEntity> list = this.list(eq);
        if (list.size() == 0 || list == null) {
            throw new LoginException("用户名不存在 请核实是否正确");
        }
        List<MemberEntity> members = list.stream().filter(i -> DigestUtils.md5Hex(i.getMobile() + memberVo.getPassword()).equals(i.getPassword())).collect(Collectors.toList());

        if (members.size() == 0 || members == null) {
            throw new LoginException("密码错误 请重新输入");
        }
        if (members.size() == 0) {
            throw new LoginException("密码错误 请重新输入");
        }
        if (members.size() > 1) {
            throw new LoginException("错误 查询到多个账号 请电话联系我们官方客服核实");
        }
        return members.get(0);
    }

    @Override
    public MemberEntity authLogin(MemberEntity memberVo) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("auth_id", memberVo.getAuthId()).eq("auth_type", memberVo.getAuthType());
        List<MemberEntity> list = this.list(wrapper);
        if (list == null || list.size() == 0) {
            this.save(memberVo);
            return memberVo;
        } else {
            memberVo.setCreateTime(null);
            MemberEntity memberEntity = list.get(0);
            memberEntity.setAuthToken(memberVo.getAuthToken());
            this.updateById(memberEntity);
            return memberEntity;
        }


    }

    private void checkPhone(String phone) {
        List<MemberEntity> list = this.list(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (list.size() > 0) {
            throw new MemberException("用户号码已经存在");
        }
    }

    private void checkUsername(String userName) {
        List<MemberEntity> list = this.list(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (list.size() > 0) {
            throw new MemberException("用户名已经存在");
        }
    }


}