package shop.jagentmall.domain;

import lombok.Data;

import java.util.List;

/**
 * @Title: EsProductRelatedInfo
 * @Author: [tianyou]
 * @Date: 2025/2/21
 * @Description: 搜索商品的品牌名称，分类名称及属性
 */
@Data
public class EsProductRelatedInfo {
    private List<String> brandNames;
    private List<String> productCategoryNames;
    private List<EsProductAttr>   productAttrs;
}
