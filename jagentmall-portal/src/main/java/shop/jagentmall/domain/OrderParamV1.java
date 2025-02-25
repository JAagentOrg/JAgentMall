package shop.jagentmall.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Title: OrderParamV1
 * @Author: [tianyou]
 * @Date: 2025/2/25
 * @Description: 订单参数信息改进版
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderParamV1 {
    @Schema(title = "收货地址ID")
    private Long memberReceiveAddressId;
    @Schema(title = "优惠券ID")
    private Long couponId;
    @Schema(title = "使用的积分数")
    private Integer useIntegration;
    @Schema(title = "支付方式")
    private Integer payType;
    @Schema(title = "被选中的购物车商品ID")
    private List<CartInfo> cartInfo;

    // 内部类 CartInfo
    @Data
    public static class CartInfo{
        @Schema(title = "购物车商品id")
        private Long cartId;
        @Schema(title = "商品id")
        private Long productId;
        @Schema(title = "商品skuId")
        private Long productSkuId;
        @Schema(title = "购买数量")
        private Integer quantity;
    }
}
