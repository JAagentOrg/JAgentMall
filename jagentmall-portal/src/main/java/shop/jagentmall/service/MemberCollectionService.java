package shop.jagentmall.service;

import org.springframework.data.domain.Page;
import shop.jagentmall.domain.MemberProductCollection;

/**
 * @Title: MemberCollectionService
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 会员收藏Service
 */
public interface MemberCollectionService {
    int add(MemberProductCollection productCollection);

    int delete(Long productId);

    Page<MemberProductCollection> list(Integer pageNum, Integer pageSize);

    MemberProductCollection detail(Long productId);

    void clear();
}
