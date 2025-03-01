package shop.jagentmall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.model.UmsMemberLevel;
import shop.jagentmall.service.UmsMemberLevelService;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/01
 * @Description: 会员等级管理Controller
 */
@Controller
@Tag(name = "UmsMemberLevelController", description = "会员等级管理")
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {

    @Autowired
    private UmsMemberLevelService memberLevelService;

    @GetMapping(value = "/list")
    @Operation(summary = "查询所有会员等级")
    @ResponseBody
    public CommonResult<List<UmsMemberLevel>> list(@RequestParam("defaultStatus") Integer defaultStatus) {
        List<UmsMemberLevel> memberLevelList = memberLevelService.list(defaultStatus);
        return CommonResult.success(memberLevelList);
    }

}
