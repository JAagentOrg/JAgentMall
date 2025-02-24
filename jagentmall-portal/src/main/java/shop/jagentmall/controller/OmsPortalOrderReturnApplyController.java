package shop.jagentmall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.domain.OmsOrderReturnApplyParam;
import shop.jagentmall.service.OmsPortalOrderReturnApplyService;

/**
 * @Title: OmsPortalOrderReturnApplyController
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 申请退货管理
 */
@Controller
@Tag(name = "OmsPortalOrderReturnApplyController", description = "申请退货管理")
@RequestMapping("/returnApply")
public class OmsPortalOrderReturnApplyController {

    @Autowired
    private OmsPortalOrderReturnApplyService returnApplyService;

    @Operation(summary = "申请退货")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody OmsOrderReturnApplyParam returnApply) {
        int count = returnApplyService.create(returnApply);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
