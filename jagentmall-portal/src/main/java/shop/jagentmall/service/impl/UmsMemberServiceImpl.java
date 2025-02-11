package shop.jagentmall.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import shop.jagentmall.constant.AuthConstant;
import shop.jagentmall.dto.UserDto;
import shop.jagentmall.exception.Asserts;
import shop.jagentmall.mapper.UmsMemberMapper;
import shop.jagentmall.model.UmsMember;
import shop.jagentmall.model.UmsMemberExample;
import shop.jagentmall.service.UmsMemberService;
import shop.jagentmall.util.StpMemberUtil;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/09/21:21
 * @Description: 会员管理Service实现类
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);

    @Autowired
    private UmsMemberMapper memberMapper;


    public UmsMember getByUsername(String username) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(memberList)) {
            return memberList.get(0);
        }
        return null;
    }

    @Override
    public SaTokenInfo login(String username, String password) {

        //数据校验
        if(StrUtil.isEmpty(username)||StrUtil.isEmpty(password)){
            Asserts.fail("用户名或密码不能为空！");
        }
        UmsMember member = getByUsername(username);
        if(member==null){
            Asserts.fail("找不到该用户！");
        }
        if (!BCrypt.checkpw(password, member.getPassword())) {
            Asserts.fail("密码不正确！");
        }
        if(member.getStatus()!=1){
            Asserts.fail("该账号已被禁用！");
        }
        // 登录校验成功后，一行代码实现登录
        StpMemberUtil.login(member.getId());
        UserDto userDto = new UserDto();
        userDto.setId(member.getId());
        userDto.setUsername(member.getUsername());
        userDto.setClientId(AuthConstant.PORTAL_CLIENT_ID);
        // 将用户信息存储到Session中
        StpMemberUtil.getSession().set(AuthConstant.STP_MEMBER_INFO,userDto);
        // 获取当前登录用户Token信息
        return StpMemberUtil.getTokenInfo();
    }
}
