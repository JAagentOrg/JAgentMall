package shop.jagentmall.service;

import shop.jagentmall.domain.AliPayParam;

import java.util.Map;

/**
 * @Title: AlipayService
 * @Author: [tianyou]
 * @Date: 2025/2/19
 * @Description: 支付宝支付服务service
 */
public interface AlipayService {
    /**
     * 根据提交参数生成电脑支付页面
     */
    String pay(AliPayParam aliPayParam);

    /**
     * 支付宝异步回调处理
     */
    String notify(Map<String, String> params);

    /**
     * 查询支付宝交易状态
     * @param outTradeNo 商户订单编号
     * @param tradeNo 支付宝交易编号
     * @return 支付宝交易状态
     */
    String query(String outTradeNo, String tradeNo);

    /**
     * 根据提交参数生成手机支付页面
     */
    String webPay(AliPayParam aliPayParam);

    /**
     * 支付关闭
     * @param outTradeNo
     */
    void colsePay(String outTradeNo);
}
