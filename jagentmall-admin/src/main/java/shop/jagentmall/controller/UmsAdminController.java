package shop.jagentmall.controller;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.dto.UmsAdminLoginParam;
import shop.jagentmall.dto.UmsAdminParam;
import shop.jagentmall.model.UmsAdmin;
import shop.jagentmall.model.UmsRole;
import shop.jagentmall.service.UmsAdminService;
import shop.jagentmall.service.UmsRoleService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Tag(name = "UmsAdminController",description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
    @Autowired
    private UmsAdminService adminService;
    @Value("${sa-token.token-prefix}")
    private String tokenHead;
    @Autowired
    private UmsRoleService roleService;


    @Operation(summary = "登录以后返回token")
    @PostMapping("/login")
    @ResponseBody
    CommonResult login(@RequestBody UmsAdminLoginParam umsAdminLoginParam){
        SaTokenInfo saTokenInfo = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (saTokenInfo  == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", saTokenInfo.getTokenValue() );
        tokenMap.put("tokenHead", tokenHead+" ");
        return CommonResult.success(tokenMap);
    }

    @Operation(summary = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<UmsAdmin> register(@Validated @RequestBody UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        if (umsAdmin == null) {
            return CommonResult.failed();
        }
        return CommonResult.success(umsAdmin);
    }

    @Operation(summary = "获取当前登录用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAdminInfo() {
        UmsAdmin umsAdmin = adminService.getCurrentAdmin();
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("menus", roleService.getMenuList(umsAdmin.getId()));
        data.put("icon", umsAdmin.getIcon());
        List<UmsRole> roleList = adminService.getRoleList(umsAdmin.getId());
        if(CollUtil.isNotEmpty(roleList)){
            List<String> roles = roleList.stream().map(UmsRole::getName).collect(Collectors.toList());
            data.put("roles",roles);
        }
        return CommonResult.success(data);
    }

    @Operation(summary = "登出功能")
    @PostMapping(value = "/logout")
    @ResponseBody
    public CommonResult logout() {
        adminService.logout();
        return CommonResult.success(null);
    }


}
