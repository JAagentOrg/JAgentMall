package shop.jagentmall.service.impl;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import shop.jagentmall.dao.UmsRoleDao;
import shop.jagentmall.mapper.UmsRoleMapper;
import shop.jagentmall.model.UmsMenu;
import shop.jagentmall.model.UmsRole;
import shop.jagentmall.model.UmsRoleExample;
import shop.jagentmall.service.UmsResourceService;
import shop.jagentmall.service.UmsRoleService;

import java.util.Date;
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

    @Autowired
    private UmsRoleMapper roleMapper;
    @Autowired
    private UmsResourceService resourceService;

    /**
     * 根据管理员ID获取对应菜单
     * @param adminId
     * @return
     */
    @Override
    public List<UmsMenu> getMenuList(Long adminId) {
        return roleDao.getMenuList(adminId);
    }

    /**
     * 创建角色
     * @param role
     * @return
     */
    @Override
    public int create(UmsRole role) {
        role.setCreateTime(new Date());
        role.setAdminCount(0);
        role.setSort(0);
        return roleMapper.insert(role);
    }

    @Override
    public int update(Long id, UmsRole role) {
        role.setId(id);
        return roleMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    public int delete(List<Long> ids) {
        UmsRoleExample example = new UmsRoleExample();
        example.createCriteria().andIdIn(ids);
        int count = roleMapper.deleteByExample(example);
        resourceService.initPathResourceMap();
        return count;
    }

    @Override
    public List<UmsRole> list() {
        return roleMapper.selectByExample(new UmsRoleExample());
    }

    @Override
    public List<UmsRole> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        UmsRoleExample example = new UmsRoleExample();
        if (!StringUtils.isEmpty(keyword)) {
            example.createCriteria().andNameLike("%" + keyword + "%");
        }
        return roleMapper.selectByExample(example);
    }
}
