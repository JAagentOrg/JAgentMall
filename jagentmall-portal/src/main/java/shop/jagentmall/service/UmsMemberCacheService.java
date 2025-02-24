package shop.jagentmall.service;

import shop.jagentmall.model.UmsMember;

/**
 * @Title: UmsMemberCacheService
 * @Author: [tianyou]
 * @Date: 2025/2/16
 * @Description: 会员缓存相关service
 */
public interface UmsMemberCacheService {
    /**
     * 删除会员用户缓存
     */
    void delMember(Long memberId);

    /**
     * 获取会员用户缓存
     */
    UmsMember getMember(Long memberId);

    /**
     * 设置会员用户缓存
     */
    void setMember(UmsMember member);

    /**
     * 设置验证码
     */
    void setAuthCode(String telephone, String authCode);

    /**
     * 获取验证码
     */
    String getAuthCode(String telephone);

    /**
     * 移除验证码
     */
    boolean removeAuthCode(String telephone);
}
