package shop.jagentmall.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import shop.jagentmall.domain.OmsOrderReturnApplyParam;
import shop.jagentmall.mapper.OmsOrderReturnApplyMapper;
import shop.jagentmall.model.OmsOrderReturnApply;
import shop.jagentmall.service.OmsPortalOrderReturnApplyService;

import java.util.Date;

/**
 * @Title: OmsPortalOrderReturnApplyServiceImpl
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 退货管理service实现类
 */
public class OmsPortalOrderReturnApplyServiceImpl implements OmsPortalOrderReturnApplyService {

    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;
    @Override
    public int create(OmsOrderReturnApplyParam returnApply) {
        OmsOrderReturnApply realApply = new OmsOrderReturnApply();
        BeanUtils.copyProperties(returnApply,realApply);
        realApply.setCreateTime(new Date());
        realApply.setStatus(0);
        return returnApplyMapper.insert(realApply);
    }
}
