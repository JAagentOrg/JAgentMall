package shop.jagentmall.service.impl;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import shop.jagentmall.constant.AuthConstant;
import shop.jagentmall.dao.UmsAdminRoleRelationDao;
import shop.jagentmall.dto.UmsAdminParam;
import shop.jagentmall.dto.UserDto;
import shop.jagentmall.exception.Asserts;
import shop.jagentmall.mapper.UmsAdminLoginLogMapper;
import shop.jagentmall.mapper.UmsAdminMapper;
import shop.jagentmall.model.*;
import shop.jagentmall.service.UmsAdminCacheService;
import shop.jagentmall.service.UmsAdminService;

import java.util.Date;
import java.util.List;

@Service
public class UmsAdminServiceImpl implements UmsAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsAdminServiceImpl.class);

    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;
    @Autowired
    private UmsAdminLoginLogMapper loginLogMapper;
    @Autowired
    private UmsAdminCacheService adminCacheService;

    @Override
    public UmsAdmin getAdminByUsername(String username) {
        UmsAdminExample umsAdminExample = new UmsAdminExample();
        umsAdminExample.createCriteria().andUsernameEqualTo(username);
        List<UmsAdmin> adminList = adminMapper.selectByExample(umsAdminExample);
        if(adminList != null && adminList.size() > 0){
            return adminList.get(0);
        }
        return null;
    }

    @Override
    public SaTokenInfo login(String username, String password) {
        if(StrUtil.isEmpty(username)||StrUtil.isEmpty(password)){
            Asserts.fail("用户名或密码不能为空！");
        }
        UmsAdmin admin = getAdminByUsername(username);
        if(admin==null){
            Asserts.fail("找不到该用户！");
        }
        if (!BCrypt.checkpw(password, admin.getPassword())) {
            Asserts.fail("密码不正确！");
        }
        if(admin.getStatus()!=1){
            Asserts.fail("该账号已被禁用！");
        }
        // 登录校验成功后，一行代码实现登录
        StpUtil.login(admin.getId());
        UserDto userDto = new UserDto();
        userDto.setId(admin.getId());
        userDto.setUsername(admin.getUsername());
        userDto.setClientId(AuthConstant.ADMIN_CLIENT_ID);
        List<UmsResource> resourceList = getResourceList(admin.getId());
        List<String> permissionList = resourceList.stream().map(item -> item.getId() + ":" + item.getName()).toList();
        userDto.setPermissionList(permissionList);
        // 将用户信息存储到Session中
        StpUtil.getSession().set(AuthConstant.STP_ADMIN_INFO,userDto);
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
        insertLoginLog(admin);
        return saTokenInfo;
    }

    /**
     * 添加登录记录
     */
    private void insertLoginLog(UmsAdmin admin) {
        if(admin == null) return;
        UmsAdminLoginLog loginLog = new UmsAdminLoginLog();
        loginLog.setAdminId(admin.getId());
        loginLog.setCreateTime(new Date());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        loginLog.setIp(request.getRemoteAddr());
        loginLogMapper.insert(loginLog);
    }

    @Override
    public List<UmsResource> getResourceList(Long adminId) {
        return adminRoleRelationDao.getResourceList(adminId);
    }

    /**
     * 注册管理员账号
     * @param umsAdminParam
     * @return
     */
    @Override
    public UmsAdmin register(UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = new UmsAdmin();
        BeanUtils.copyProperties(umsAdminParam, umsAdmin);
        umsAdmin.setCreateTime(new Date());
        umsAdmin.setStatus(1);
        //查询是否有相同用户名的用户
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(umsAdmin.getUsername());
        List<UmsAdmin> umsAdminList = adminMapper.selectByExample(example);
        if (umsAdminList.size() > 0) {
            return null;
        }
        //将密码进行加密操作
        String encodePassword = BCrypt.hashpw(umsAdmin.getPassword());
        umsAdmin.setPassword(encodePassword);
        adminMapper.insert(umsAdmin);
        return umsAdmin;
    }

    /**
     * 获取当前登录后台用户信息
     * @return
     */
    @Override
    public UmsAdmin getCurrentAdmin() {
        UserDto userDto = (UserDto) StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
        UmsAdmin admin = adminCacheService.getAdmin(userDto.getId());
        if (admin == null) {
            admin = adminMapper.selectByPrimaryKey(userDto.getId());
            adminCacheService.setAdmin(admin);
        }
        return admin;
    }

    /**
     * 获取用户对于角色
     * @param adminId
     * @return
     */
    @Override
    public List<UmsRole> getRoleList(Long adminId) {
        return adminRoleRelationDao.getRoleList(adminId);
    }

    /**
     * 注销登录
     */
    @Override
    public void logout() {
        //先清空缓存
        UserDto userDto = (UserDto) StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
        //清空 redis 缓存
        adminCacheService.delAdmin(userDto.getId());
        //再调用sa-token的登出方法
        StpUtil.logout();
    }
}
