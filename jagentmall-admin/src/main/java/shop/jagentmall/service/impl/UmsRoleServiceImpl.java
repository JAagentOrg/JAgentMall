package shop.jagentmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.jagentmall.dao.UmsRoleDao;
import shop.jagentmall.model.UmsMenu;
import shop.jagentmall.service.UmsRoleService;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/28
 * @Description: 后台角色管理Service实现类
 */
@Service
public class UmsRoleServiceImpl implements UmsRoleService {

    @Autowired
    private UmsRoleDao roleDao;

    /**
     * 根据管理员ID获取对应菜单
     * @param adminId
     * @return
     */
    @Override
    public List<UmsMenu> getMenuList(Long adminId) {
        return roleDao.getMenuList(adminId);
    }

}
