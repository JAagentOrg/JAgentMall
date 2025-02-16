package shop.jagentmall.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import org.springframework.transaction.annotation.Transactional;
import shop.jagentmall.model.UmsMember;

import java.util.Date;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/09/21:19
 * @Description: 会员管理Service
 */
public interface UmsMemberService {

    /**
     * 会员登录后获取 token
     */
    SaTokenInfo login(String username, String password);

    /**
     * 会员注册
     * @param username
     * @param password
     * @param telephone
     * @param authCode
     */
    @Transactional
    void register(String username, String password, String telephone, String authCode);

    /**
     * 获取当前会话中的用户信息
     * @return
     */
    UmsMember getCurrentMember();

    /**
     * 通过用户id获取用户信息
     * @param id
     * @return
     */
    UmsMember getById(Long id);

    /**
     * 会员登出
     */
    void logout();

    /**
     * 获取验证码
     * @param telephone
     * @return
     */
    String generateAuthCode(String telephone);

    /**
     * 修改密码
     * @param telephone
     * @param password
     * @param authCode
     */
    @Transactional
    void updatePassword(String telephone, String password, String authCode);

    /**
     * 更新用户信息
     * @param nickname
     * @param icon
     * @param gender
     * @param birthday
     * @param city
     * @param job
     * @param personalizedSignature
     */
    @Transactional
    boolean updateUserInfo(String nickname, String icon, Integer gender, Date birthday, String city, String job, String personalizedSignature);

    /**
     * 删除用户
     * @param telephone
     * @param password
     * @param authCode
     */
    @Transactional
    void delUser(String telephone, String password, String authCode);
}
