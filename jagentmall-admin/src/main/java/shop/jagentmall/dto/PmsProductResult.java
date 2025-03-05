package shop.jagentmall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/02
 * @Description: 查询单个产品进行修改时返回的结果
 */
public class PmsProductResult extends PmsProductParam {

    @Getter
    @Setter
    @Schema(title = "商品所选分类的父id")
    private Long cateParentId;

}
