package shop.jagentmall.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import shop.jagentmall.dto.UmsAdminParam;
import shop.jagentmall.model.UmsAdmin;
import shop.jagentmall.model.UmsResource;
import shop.jagentmall.model.UmsRole;

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

    /**
     * 获取当前登录后台用户
     */
    UmsAdmin getCurrentAdmin();

    /**
     * 获取用户对于角色
     */
    List<UmsRole> getRoleList(Long adminId);

    /**
     * 登出操作
     */
    void logout();
}
