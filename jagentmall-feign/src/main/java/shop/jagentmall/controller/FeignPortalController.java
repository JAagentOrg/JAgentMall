package shop.jagentmall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.jagentmall.api.CommonPage;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.domain.OmsOrderDetail;
import shop.jagentmall.service.PortalFeignClient;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/03
 * @Description: Feign调用jagentmall-portal接口示例
 */
@Tag(name = "FeignPortalController", description = "Feign调用jagentmall-portal接口示例")
@RestController
@RequestMapping("/feign/portal")
public class FeignPortalController {

    @Autowired
    private PortalFeignClient portalFeignClient;

    @Operation(summary = "前台会员列表登录")
    @PostMapping("/login")
    public CommonResult login(@RequestParam String username, @RequestParam String password) {
        return portalFeignClient.login(username,password);
    }

    @Operation(summary = "获取用户订单")
    @GetMapping("/orderList")
    public CommonResult<CommonPage<OmsOrderDetail>> orderList(@RequestParam Integer status,
                                                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                              @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        return portalFeignClient.orderList(status, pageNum, pageSize);
    }
}

