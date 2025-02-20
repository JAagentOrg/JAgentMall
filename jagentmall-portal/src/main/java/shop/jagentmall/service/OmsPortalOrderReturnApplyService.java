package shop.jagentmall.service;

import shop.jagentmall.domain.OmsOrderReturnApplyParam;

/**
 * @Title: OmsPortalOrderReturnApplyService
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 退货管理service
 */
public interface OmsPortalOrderReturnApplyService {
    /**
     * 提交申请
     */
    int create(OmsOrderReturnApplyParam returnApply);
}
