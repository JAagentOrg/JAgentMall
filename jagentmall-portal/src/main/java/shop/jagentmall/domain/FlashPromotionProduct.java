package shop.jagentmall.domain;

import lombok.Data;
import shop.jagentmall.model.PmsProduct;

import java.math.BigDecimal;

/**
 * @Title: FlashPromotionProduct
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 秒杀商品封装类
 */
@Data
public class FlashPromotionProduct extends PmsProduct {
    private BigDecimal flashPromotionPrice;
    private Integer flashPromotionCount;
    private Integer flashPromotionLimit;
}
