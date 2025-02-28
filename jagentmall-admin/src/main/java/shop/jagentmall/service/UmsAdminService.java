package shop.jagentmall.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import shop.jagentmall.dto.UmsAdminParam;
import shop.jagentmall.model.UmsAdmin;
import shop.jagentmall.model.UmsResource;

import java.util.List;

public interface UmsAdminService {

    /**
     * 根据用户名获取后台管理员
     */
    UmsAdmin getAdminByUsername(String username);

    /**
     * 登录功能
     * @param username 用户名
     * @param password 密码
     */
    SaTokenInfo login(String username, String password);

    /**
     * 获取指定用户的可访问资源
     */
    List<UmsResource> getResourceList(Long adminId);


    /**
     * 注册功能
     */
    UmsAdmin register(UmsAdminParam umsAdminParam);
}
