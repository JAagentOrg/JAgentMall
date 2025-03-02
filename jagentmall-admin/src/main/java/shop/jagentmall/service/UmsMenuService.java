package shop.jagentmall.service;

import shop.jagentmall.model.UmsMenu;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/02
 * @Description: 后台菜单管理Service
 */
public interface UmsMenuService {

    /**
     * 创建后台菜单
     */
    int create(UmsMenu umsMenu);

    /**
     * 修改后台菜单
     */
    int update(Long id, UmsMenu umsMenu);

    /**
     * 根据ID获取菜单详情
     */
    UmsMenu getItem(Long id);

}
