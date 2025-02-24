package shop.jagentmall.service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import shop.jagentmall.domain.MemberBrandAttention;

/**
 * @Title: MemberAttentionService
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 会员关注品牌管理service
 */
public interface MemberAttentionService {
    /**
     * 添加关注
     */
    @Transactional
    int add(MemberBrandAttention memberBrandAttention);

    /**
     * 取消关注
     */
    @Transactional
    int delete(Long brandId);

    /**
     * 获取用户关注列表
     */
    Page<MemberBrandAttention> list(Integer pageNum, Integer pageSize);

    /**
     * 获取用户关注详情
     */
    MemberBrandAttention detail(Long brandId);

    /**
     * 清空关注列表
     */
    void clear();
}
