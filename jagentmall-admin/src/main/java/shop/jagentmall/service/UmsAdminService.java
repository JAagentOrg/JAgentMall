package shop.jagentmall.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import org.springframework.transaction.annotation.Transactional;
import shop.jagentmall.dto.UmsAdminParam;
import shop.jagentmall.dto.UpdateAdminPasswordParam;
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

    /**
     * 根据用户名或昵称分页查询用户
     */
    List<UmsAdmin> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 根据用户id获取用户
     */
    UmsAdmin getItem(Long id);

    /**
     * 修改密码
     */
    int updatePassword(UpdateAdminPasswordParam param);

    /**
     * 修改指定用户信息
     */
    int update(Long id, UmsAdmin admin);

    /**
     * 删除指定用户
     */
    int delete(Long id);

    /**
     * 修改用户角色关系
     */
    @Transactional
    int updateRole(Long adminId, List<Long> roleIds);
}
