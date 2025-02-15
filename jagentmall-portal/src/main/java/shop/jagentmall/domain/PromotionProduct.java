package shop.jagentmall.domain;

import lombok.Data;
import shop.jagentmall.model.PmsProduct;
import shop.jagentmall.model.PmsProductFullReduction;
import shop.jagentmall.model.PmsProductLadder;
import shop.jagentmall.model.PmsSkuStock;

import java.util.List;

/**
 * @Title: PromotionProduct
 * @Author: [tianyou]
 * @Date: 2025/2/15 23:18
 * @Description: 商品促销信息，包括SKU、打折优惠、满减优惠
 */
@Data
public class PromotionProduct extends PmsProduct {
    //商品库存信息
    private List<PmsSkuStock> skuStockList;
    //商品打折信息
    private List<PmsProductLadder> productLadderList;
    //商品满减信息
    private List<PmsProductFullReduction> productFullReductionList;
}
