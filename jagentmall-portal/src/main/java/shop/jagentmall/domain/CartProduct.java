package shop.jagentmall.domain;
import lombok.Data;
import shop.jagentmall.model.PmsProduct;
import shop.jagentmall.model.PmsProductAttribute;
import shop.jagentmall.model.PmsSkuStock;

import java.util.List;

/**
 * @Title: CartProduct
 * @Author: [tianyou]
 * @Date: 2025/2/15 23:34
 * @Description: 购物车中选择规格的商品信息
 */
@Data
public class CartProduct extends PmsProduct {
    private List<PmsProductAttribute> productAttributeList;
    private List<PmsSkuStock> skuStockList;
}
