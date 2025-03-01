package shop.jagentmall.service;

import shop.jagentmall.model.UmsMenu;
import shop.jagentmall.model.UmsRole;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/28
 * @Description: 后台角色管理Service
 */
public interface UmsRoleService {

    /**
     * 根据管理员ID获取对应菜单
     */
    List<UmsMenu> getMenuList(Long adminId);

    /**
     * 添加角色
     */
    int create(UmsRole role);
    /**
     * 修改角色信息
     */
    int update(Long id, UmsRole role);

    /**
     * 批量删除角色
     */
    int delete(List<Long> ids);

    /**
     * 获取所有角色列表
     */
    List<UmsRole> list();

}
