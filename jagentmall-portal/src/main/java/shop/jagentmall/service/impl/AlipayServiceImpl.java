package shop.jagentmall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.jagentmall.component.ClosePaySender;
import shop.jagentmall.config.AlipayConfig;
import shop.jagentmall.domain.AliPayParam;
import shop.jagentmall.domain.AliPayRefundParam;
import shop.jagentmall.mapper.OmsOrderMapper;
import shop.jagentmall.service.AlipayService;
import shop.jagentmall.service.OmsPortalOrderService;

import java.util.Map;

/**
 * @Title: AlipayServiceImpl
 * @Author: [tianyou]
 * @Date: 2025/2/19
 * @Description: 支付宝支付服务service实现类
 */
@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService {
    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsPortalOrderService portalOrderService;
    @Autowired
    private ClosePaySender closePaySender;
    @Override
    public String pay(AliPayParam aliPayParam) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        if(StrUtil.isNotEmpty(alipayConfig.getNotifyUrl())){
            //异步接收地址，公网可访问
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
        }
        if(StrUtil.isNotEmpty(alipayConfig.getReturnUrl())){
            //同步跳转地址
            request.setReturnUrl(alipayConfig.getReturnUrl());
        }
        //******必传参数******
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", aliPayParam.getOutTradeNo());
        //支付金额，最小值0.01元
        bizContent.put("total_amount", aliPayParam.getTotalAmount());
        //订单标题，不可使用特殊符号
        bizContent.put("subject", aliPayParam.getSubject());
        //电脑网站支付场景固定传值FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        String formHtml = null;
        closePaySender.sendMessage(aliPayParam.getOutTradeNo(),5*60*1000);
        try {
//            formHtml = alipayClient.pageExecute(request).getBody();
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                // 支付成功
                formHtml = response.getBody();
            }
            else{
                // 支付失败
                String errorMessage = "Alipay payment failed: " + response.getSubMsg();
                // 可以记录日志、抛出自定义异常等
                log.error(errorMessage);
                // 根据实际需求返回错误信息或者提示用户
                formHtml = "<h1>支付失败: " + errorMessage + "</h1>";
            }
        } catch (AlipayApiException e) {
            log.error("Alipay API Exception: " + e.getMessage(), e);
            formHtml = "<h1>支付请求发生错误，请稍后再试</h1>";
        }
        return formHtml;
    }

    @Override
    public String notify(Map<String, String> params) {
        String result = "failure";
        boolean signVerified = false;
        try {
            //调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
        } catch (AlipayApiException e) {
            log.error("支付回调签名校验异常！",e);
            e.printStackTrace();
        }
        if (signVerified) {
            String tradeStatus = params.get("trade_status");
            if("TRADE_SUCCESS".equals(tradeStatus)){
                result = "success";
                log.info("notify方法被调用了，tradeStatus:{}",tradeStatus);
                String outTradeNo = params.get("out_trade_no");
                portalOrderService.paySuccessByOrderSn(outTradeNo,1);
            }else{
                log.warn("订单未支付成功，trade_status:{}",tradeStatus);
            }
        } else {
            log.warn("支付回调签名校验失败！");
        }
        return result;
    }

    @Override
    public String query(String outTradeNo, String tradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //******必传参数******
        JSONObject bizContent = new JSONObject();
        //设置查询参数，out_trade_no和trade_no至少传一个
        if(StrUtil.isNotEmpty(outTradeNo)){
            bizContent.put("out_trade_no",outTradeNo);
        }
        if(StrUtil.isNotEmpty(tradeNo)){
            bizContent.put("trade_no",tradeNo);
        }
        //交易结算信息: trade_settle_info
        String[] queryOptions = {"trade_settle_info"};
        bizContent.put("query_options", queryOptions);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("查询支付宝账单异常！",e);
        }
        if(response.isSuccess()){
            log.info("查询支付宝账单成功！");
            if("TRADE_SUCCESS".equals(response.getTradeStatus())){
//                portalOrderService.paySuccessByOrderSn(outTradeNo,1);
            }
        } else {
            log.error("查询支付宝账单失败！");
        }
        //交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
        return response.getTradeStatus();
    }

    @Override
    public String webPay(AliPayParam aliPayParam) {
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest ();
        if(StrUtil.isNotEmpty(alipayConfig.getNotifyUrl())){
            //异步接收地址，公网可访问
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
        }
        if(StrUtil.isNotEmpty(alipayConfig.getReturnUrl())){
            //同步跳转地址
            request.setReturnUrl(alipayConfig.getReturnUrl());
        }
        //******必传参数******
        JSONObject bizContent = new JSONObject();
        //商户订单号，商家自定义，保持唯一性
        bizContent.put("out_trade_no", aliPayParam.getOutTradeNo());
        //支付金额，最小值0.01元
        bizContent.put("total_amount", aliPayParam.getTotalAmount());
        //订单标题，不可使用特殊符号
        bizContent.put("subject", aliPayParam.getSubject());
        //手机网站支付默认传值FAST_INSTANT_TRADE_PAY
        bizContent.put("product_code", "QUICK_WAP_WAY");
        request.setBizContent(bizContent.toString());
        closePaySender.sendMessage(aliPayParam.getOutTradeNo(),5*60*1000);
        String formHtml = null;
        try {
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                // 支付成功
                formHtml = response.getBody();
            }
            else{
                // 支付失败
                String errorMessage = "Alipay payment failed: " + response.getSubMsg();
                // 可以记录日志、抛出自定义异常等
                log.error(errorMessage);
                // 根据实际需求返回错误信息或者提示用户
                formHtml = "<h1>支付失败: " + errorMessage + "</h1>";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return formHtml;
    }

    @Override
    public void colsePay(String outTradeNo) {
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        request.setBizContent(bizContent.toString());
        try{
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                log.info("订单支付取消成功！outTradeNo:{}",outTradeNo);
            }
            else{
                log.info("订单支付取消失败！outTradeNo:{}",outTradeNo);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.error("Alipay API Exception: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean refund(AliPayRefundParam refundParam) {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", refundParam.getOutTradeNo());
        bizContent.put("refund_amount", refundParam.getRefundAmount());
        bizContent.put("refund_reason", refundParam.getRefundReason());
        bizContent.put("out_request_no", refundParam.getOutRefundNo());
        request.setBizContent(bizContent.toString());
        try{
            AlipayTradeRefundResponse response =  alipayClient.execute(request);
            if(response.isSuccess()){
                log.info("订单退款成功！outTradeNo:{}",refundParam.getOutTradeNo());
                return true;
            }
            else{
                log.info("订单退款失败！outTradeNo:{}",refundParam.getOutTradeNo());
                return false;
            }

        } catch (AlipayApiException e) {
            log.error("Alipay API Exception: " + e.getMessage(), e);
            return false;
        }
    }
}
