package shop.jagentmall.domain;

import lombok.Data;

import java.util.List;

/**
 * @Title: EsProductAttr
 * @Author: [tianyou]
 * @Date: 2025/2/21
 * @Description: 商品属性参数
 */
@Data
public class EsProductAttr {
    private Long attrId;
    private String attrName;
    private List<String> attrValues;
}
