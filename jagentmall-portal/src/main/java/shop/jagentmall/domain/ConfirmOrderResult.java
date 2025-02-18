package shop.jagentmall.domain;

import lombok.Data;
import shop.jagentmall.model.UmsIntegrationConsumeSetting;
import shop.jagentmall.model.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @Title: ConfirmOrderResult
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 购物车下单确认单
 */
@Data
public class ConfirmOrderResult {
    //包含优惠信息的购物车信息
    private List<CartPromotionItem> cartPromotionItemList;
    //用户收货地址列表
    private List<UmsMemberReceiveAddress> memberReceiveAddressList;
    //用户可用优惠券列表
    private List<SmsCouponHistoryDetail> couponHistoryDetailList;
    //积分使用规则
    private UmsIntegrationConsumeSetting integrationConsumeSetting;
    //会员持有的积分
    private Integer memberIntegration;
    //计算的金额
    private CalcAmount calcAmount;
}
