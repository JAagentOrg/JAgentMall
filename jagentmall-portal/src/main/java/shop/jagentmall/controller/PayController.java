package shop.jagentmall.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import shop.jagentmall.api.CommonResult;
import shop.jagentmall.config.AlipayConfig;
import shop.jagentmall.domain.AliPayParam;
import shop.jagentmall.domain.AliPayRefundParam;
import shop.jagentmall.service.AlipayService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: PayController
 * @Author: [tianyou]
 * @Date: 2025/2/19
 * @Description: 支付管理
 */
@Controller
@Tag(name = "PayController", description = "支付相关接口")
@RequestMapping("/pay")
public class PayController {
    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private AlipayService alipayService;

    @Operation(summary = "支付宝电脑网站支付")
    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    public void pay(AliPayParam aliPayParam, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=" + alipayConfig.getCharset());
        response.getWriter().write(alipayService.pay(aliPayParam));
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Operation(summary = "支付宝手机网站支付")
    @RequestMapping(value = "/webPay", method = RequestMethod.GET)
    public void webPay(AliPayParam aliPayParam, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=" + alipayConfig.getCharset());
        response.getWriter().write(alipayService.webPay(aliPayParam));
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Operation(summary = "支付宝异步回调",description = "必须为POST请求，执行成功返回success，执行失败返回failure")
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public String notify(HttpServletRequest request){
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }
        return alipayService.notify(params);
    }

    @Operation(summary = "支付宝统一收单线下交易查询",description = "订单支付成功返回交易状态：TRADE_SUCCESS")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> query(String outTradeNo, String tradeNo){
        return CommonResult.success(alipayService.query(outTradeNo,tradeNo));
    }

    @Operation(summary = "支付宝支付过程中取消支付")
    @RequestMapping(value = "/closePay", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> query(@RequestParam String outTradeNo){
        alipayService.colsePay(outTradeNo);
        return CommonResult.success(null,"支付取消成功");
    }

    @Operation(summary = "退款")
    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String> refund(AliPayRefundParam refundParam){
        alipayService.refund(refundParam);
        return CommonResult.success(null,"支付取消成功");
    }
}
