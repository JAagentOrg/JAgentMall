package shop.jagentmall.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import shop.jagentmall.constant.AuthConstant;
import shop.jagentmall.dto.UserDto;
import shop.jagentmall.exception.Asserts;
import shop.jagentmall.mapper.UmsMemberLevelMapper;
import shop.jagentmall.mapper.UmsMemberMapper;
import shop.jagentmall.model.UmsMember;
import shop.jagentmall.model.UmsMemberExample;
import shop.jagentmall.model.UmsMemberLevel;
import shop.jagentmall.model.UmsMemberLevelExample;
import shop.jagentmall.service.UmsMemberCacheService;
import shop.jagentmall.service.UmsMemberService;
import shop.jagentmall.util.StpMemberUtil;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/09/21:21
 * @Description: 会员管理Service实现类
 */
@Service
@Slf4j
public class UmsMemberServiceImpl implements UmsMemberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);

    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private UmsMemberCacheService memberCacheService;
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;


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
        if(StrUtil.isEmpty(username) || StrUtil.isEmpty(password)){
            Asserts.fail("用户名或密码不能为空！");
        }
        UmsMember member = getByUsername(username);
        log.info("============================================");
        if(member == null){
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

    @Override
    public void register(String username, String password, String telephone, String authCode) {
        if(!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        //查询是否已有该用户
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        example.or(example.createCriteria().andPhoneEqualTo(telephone));
        List<UmsMember> umsMembers = memberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(umsMembers)) {
            Asserts.fail("该用户已经存在");
        }
        //没有该用户进行添加操作
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(username);
        umsMember.setPhone(telephone);
        umsMember.setPassword(BCrypt.hashpw(password));
        umsMember.setCreateTime(new Date());
        umsMember.setStatus(1);
        //获取默认会员等级并设置
        UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
        levelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
        if (!CollectionUtils.isEmpty(memberLevelList)) {
            umsMember.setMemberLevelId(memberLevelList.get(0).getId());
        }
        memberMapper.insert(umsMember);
        umsMember.setPassword(null);
        memberCacheService.removeAuthCode(telephone);
    }

    @Override
    public UmsMember getCurrentMember() {
        UserDto userDto = (UserDto) StpMemberUtil.getSession().get(AuthConstant.STP_MEMBER_INFO);
        UmsMember member = memberCacheService.getMember(userDto.getId());
        if(member != null){
            return member;
        }else{
            member = getById(userDto.getId());
            memberCacheService.setMember(member);
            return member;
        }
    }

    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public String getCurrentMemberId() {
        UmsMember member = getCurrentMember();
        return String.valueOf(member.getId());
    }

    @Override
    public void logout() {
        //清空缓存
        UserDto userDto = (UserDto) StpMemberUtil.getSession().get(AuthConstant.STP_MEMBER_INFO);
        memberCacheService.delMember(userDto.getId());
        //登出
        StpMemberUtil.logout();
    }

    @Override
    public String generateAuthCode(String telephone) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<6;i++){
            sb.append(random.nextInt(10));
        }
        memberCacheService.setAuthCode(telephone,sb.toString());
        return sb.toString();
    }

    @Override
    public void updatePassword(String telephone, String password, String authCode) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(telephone);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(memberList)){
            Asserts.fail("该账号不存在");
        }
        //验证验证码
        if(!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        UmsMember umsMember = memberList.get(0);
        umsMember.setPassword(BCrypt.hashpw(password));
        memberMapper.updateByPrimaryKeySelective(umsMember);
        memberCacheService.delMember(umsMember.getId());
        memberCacheService.removeAuthCode(telephone);

    }

    @Override
    public boolean updateUserInfo(String nickname, String icon, Integer gender, Date birthday, String city, String job, String personalizedSignature) {
        UmsMember member = getCurrentMember();
        String phone = member.getPhone();
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(phone);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(memberList)){
            Asserts.fail("该账号不存在");
        }
        if(nickname != null){
            member.setNickname(nickname);
        }
        if(icon != null){
            member.setIcon(icon);
        }
        if(gender != null){
            if(gender != 1 && gender != 0 && gender != 2){
                return false;
            }
            member.setGender(gender);
        }
        if(birthday != null){
            member.setBirthday(birthday);
        }
        if(city != null){
            member.setCity(city);
        }
        if(job != null){
            member.setJob(job);
        }
        if(personalizedSignature != null){
            member.setPersonalizedSignature(personalizedSignature);
        }
        memberMapper.updateByPrimaryKeySelective(member);
        memberCacheService.delMember(member.getId());
        return true;
    }

    @Override
    public void delUser(String telephone, String password, String authCode) {
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(telephone);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(memberList)){
            Asserts.fail("该账号不存在");
        }
        //验证验证码
        if(!verifyAuthCode(authCode,telephone)){
            Asserts.fail("验证码错误");
        }
        UmsMember umsMember = memberList.get(0);
        umsMember.setPassword(BCrypt.hashpw(password));
        memberMapper.deleteByPrimaryKey(umsMember.getId());
        memberCacheService.delMember(umsMember.getId());
        memberCacheService.removeAuthCode(telephone);
        StpMemberUtil.logout();
    }

    @Override
    public void updateIntegration(Long id, Integer num) {
        UmsMember record=new UmsMember();
        record.setId(id);
        record.setIntegration(num);
        memberMapper.updateByPrimaryKeySelective(record);
        memberCacheService.delMember(id);
    }


    /**
     * 验证验证码是否正确
     * @param authCode
     * @param telephone
     * @return
     */
    private boolean verifyAuthCode(String authCode, String telephone){
        if(StringUtils.isEmpty(authCode)){
            return false;
        }
        String realAuthCode = memberCacheService.getAuthCode(telephone);
        log.info("真实的验证码：{}",realAuthCode);
        return authCode.equals(realAuthCode);
    }
}
