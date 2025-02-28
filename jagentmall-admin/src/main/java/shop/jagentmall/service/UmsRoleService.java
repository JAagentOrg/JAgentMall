package shop.jagentmall.service;

import shop.jagentmall.model.UmsMenu;

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

}
