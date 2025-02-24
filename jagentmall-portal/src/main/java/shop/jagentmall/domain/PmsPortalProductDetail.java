package shop.jagentmall.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import shop.jagentmall.model.*;

import java.util.List;

/**
 * @Title: PmsPortalProductDetail
 * @Author: [tianyou]
 * @Date: 2025/2/15
 * @Description: 前台商品详情
 */
@Data
public class PmsPortalProductDetail {
    @Schema(title = "商品信息")
    private PmsProduct product;
    @Schema(title = "商品品牌")
    private PmsBrand brand;
    @Schema(title = "商品属性与参数")
    private List<PmsProductAttribute> productAttributeList;
    @Schema(title = "手动录入的商品属性与参数值")
    private List<PmsProductAttributeValue> productAttributeValueList;
    @Schema(title = "商品的sku库存信息")
    private List<PmsSkuStock> skuStockList;
    @Schema(title = "商品阶梯价格设置")
    private List<PmsProductLadder> productLadderList;
    @Schema(title = "商品满减价格设置")
    private List<PmsProductFullReduction> productFullReductionList;
    @Schema(title = "商品可用优惠券")
    private List<SmsCoupon> couponList;
}
