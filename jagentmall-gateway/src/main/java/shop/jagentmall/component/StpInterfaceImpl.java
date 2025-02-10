package shop.jagentmall.component;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import shop.jagentmall.constant.AuthConstant;
import shop.jagentmall.dto.UserDto;

import java.util.List;

public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 返回此loginId拥有的权限码列表
        if(StpUtil.getLoginType().equals(loginType)){
            //后台用户需返回
            UserDto userdto = (UserDto) StpUtil.getSession().get(AuthConstant.STP_ADMIN_INFO);
            return userdto.getPermissionList();
        }else{
            //前台用户无需返回
            return null;
        }
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 返回此 loginId 拥有的角色码列表
        return null;
    }
}
