package shop.jagentmall.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Title: CalcAmount
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 确认订单计算金额
 */
@Data
public class CalcAmount {
    private BigDecimal totalAmount;
    //运费
    private BigDecimal freightAmount;
    //活动优惠
    private BigDecimal promotionAmount;
    //应付金额
    private BigDecimal payAmount;
}
