package shop.jagentmall.service;

import cn.dev33.satoken.stp.SaTokenInfo;

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
}
