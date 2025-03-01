package shop.jagentmall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.model.UmsRole;
import shop.jagentmall.service.UmsRoleService;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/01
 * @Description: 后台用户角色管理
 */
@Controller
@Tag(name = "UmsRoleController", description = "后台用户角色管理")
@RequestMapping("/role")
public class UmsRoleController {

    @Autowired
    private UmsRoleService roleService;

    @Operation(summary = "添加角色")
    @PostMapping(value = "/create")
    @ResponseBody
    public CommonResult create(@RequestBody UmsRole role) {
        int count = roleService.create(role);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

}
