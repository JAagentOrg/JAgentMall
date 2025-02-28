package shop.jagentmall.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Title: AliPayRefundParam
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 支付宝退款参数
 */
@Data
public class AliPayRefundParam {
    /**
     * 商户订单号，商家自定义，保持唯一性
     */
    private String outTradeNo;

    /**
     * 退款金额，单位为元，精确到小数点后两位
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;
    /**
     * 退款请求号 (防止重复退款)
     */
    private String outRefundNo;
}
