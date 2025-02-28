package shop.jagentmall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.jagentmall.api.CommonPage;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.domain.ConfirmOrderResult;
import shop.jagentmall.domain.OmsOrderDetail;
import shop.jagentmall.domain.OrderParam;
import shop.jagentmall.domain.OrderParamV1;
import shop.jagentmall.idempotent.annotation.Idempotent;
import shop.jagentmall.idempotent.enums.IdempotentSceneEnum;
import shop.jagentmall.idempotent.enums.IdempotentTypeEnum;
import shop.jagentmall.service.OmsPortalOrderService;

import java.util.List;
import java.util.Map;

/**
 * @Title: OmsPortalOrderController
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 订单管理
 */

@Controller
@Tag(name = "OmsPortalOrderController", description = "订单管理")
@RequestMapping("/order")
public class OmsPortalOrderController {
    @Autowired
    private OmsPortalOrderService portalOrderService;

    @Operation(summary = "根据购物车信息生成确认单信息")
    @RequestMapping(value = "/generateConfirmOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<ConfirmOrderResult> generateConfirmOrder(@RequestBody List<Long> cartIds) {
        ConfirmOrderResult confirmOrderResult = portalOrderService.generateConfirmOrder(cartIds);
        return CommonResult.success(confirmOrderResult);
    }

    @Idempotent(
            uniqueKeyPrefix = "JAgentMall-PortalOrder:lock_generateOrder:",
            key = "T(shop.jagentmall.context.SpringContextHolder).getBean('environment').getProperty('unique-name', '')"
                    + "+'_'+"
                    + "T(shop.jagentmall.context.SpringContextHolder).getBean('umsMemberServiceImpl').getCurrentMemberId()",
            message = "正在执行下单流程，请稍后...",
            scene = IdempotentSceneEnum.RESTAPI,
            type = IdempotentTypeEnum.SPEL
    )
    @Operation(summary = "根据购物车信息生成订单")
    @RequestMapping(value = "/generateOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult generateOrder(@RequestBody OrderParam orderParam) {
        Map<String, Object> result = portalOrderService.generateOrder(orderParam);
        return CommonResult.success(result, "下单成功");
    }

    @Idempotent(
            uniqueKeyPrefix = "JAgentMall-PortalOrder:lock_generateOrderV1:",
            key = "T(shop.jagentmall.context.SpringContextHolder).getBean('environment').getProperty('unique-name', '')"
                    + "+'_'+"
                    + "T(shop.jagentmall.context.SpringContextHolder).getBean('umsMemberServiceImpl').getCurrentMemberId()",
            message = "正在执行下单流程，请稍后...",
            scene = IdempotentSceneEnum.RESTAPI,
            type = IdempotentTypeEnum.SPEL
    )
    @Operation(summary = "根据购物车信息生成订单")
    @RequestMapping(value = "/generateOrderV1", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult generateOrderV1(@RequestBody OrderParamV1 orderParam) {
        Map<String, Object> result = portalOrderService.generateOrderV1(orderParam);
        return CommonResult.success(result, "下单成功");
    }


    @Operation(summary = "按状态分页获取用户订单列表")
    @Parameter(name = "status", description = "订单状态：-1->全部；0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭",
            in = ParameterIn.QUERY, schema = @Schema(type = "integer",defaultValue = "-1",allowableValues = {"-1","0","1","2","3","4"}))
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrderDetail>> list(@RequestParam Integer status,
                                                         @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                         @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        CommonPage<OmsOrderDetail> orderPage = portalOrderService.list(status,pageNum,pageSize);
        return CommonResult.success(orderPage);
    }

    @Operation(summary = "根据ID获取订单详情")
    @RequestMapping(value = "/detail/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsOrderDetail> detail(@PathVariable Long orderId) {
        OmsOrderDetail orderDetail = portalOrderService.detail(orderId);
        return CommonResult.success(orderDetail);
    }

    @Operation(summary = "取消订单")
    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelUserOrder(Long orderId) {
        portalOrderService.cancelOrder(orderId);
        return CommonResult.success(null);
    }

    @Operation(summary = "用户确认收货")
    @RequestMapping(value = "/confirmReceiveOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult confirmReceiveOrder(Long orderId) {
        portalOrderService.confirmReceiveOrder(orderId);
        return CommonResult.success(null);
    }

    @Operation(summary = "用户删除订单")
    @RequestMapping(value = "/deleteOrder", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteOrder(Long orderId) {
        portalOrderService.deleteOrder(orderId);
        return CommonResult.success(null);
    }

    @Operation(summary = "用户支付成功的回调")
    @RequestMapping(value = "/paySuccess", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult paySuccess(@RequestParam Long orderId,@RequestParam Integer payType) {
        Integer count = portalOrderService.paySuccess(orderId,payType);
        return CommonResult.success(count, "支付成功");
    }



}
