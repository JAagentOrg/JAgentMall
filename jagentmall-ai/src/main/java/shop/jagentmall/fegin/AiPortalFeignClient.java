package shop.jagentmall.fegin;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import shop.jagentmall.api.CommonPage;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.component.FeignRequestInterceptor;
import shop.jagentmall.config.FeignConfig;
import shop.jagentmall.domain.OmsOrderDetail;
import shop.jagentmall.model.OmsCartItem;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/11
 * @Description:
 */
@FeignClient(name = "jagentmall-portal", configuration = FeignRequestInterceptor.class)
public interface AiPortalFeignClient {
    // 与PortalFeignClient相同的方法
    @PostMapping("/sso/login")
    CommonResult login(@RequestParam String username, @RequestParam String password);

    @GetMapping("/cart/list")
    CommonResult list(@RequestHeader("Authorization") String token);



    @GetMapping("/order/list")
    CommonResult<CommonPage<OmsOrderDetail>> orderList(@RequestParam("status") Integer status,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize, @RequestHeader("Authorization") String token);

    @GetMapping("/order/list")
    CommonResult<CommonPage<OmsOrderDetail>> orderList(@RequestParam("status") Integer status,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize);

    @Operation(summary = "AI专用,通过商品名称获取购物车商品id")
    @RequestMapping(value = "/cart/ai_cart_list", method = RequestMethod.GET)
    CommonResult<List<OmsCartItem>> aiCartListName(@RequestParam String productName);


    @Operation(summary = "根据购物车信息生成订单")
    @RequestMapping(value = "/order/ai_generateOrder", method = RequestMethod.POST)
    CommonResult aiGenerateOrder(@RequestBody List<Long> cartIds);
}
