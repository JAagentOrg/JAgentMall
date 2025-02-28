package shop.jagentmall.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import shop.jagentmall.domain.MemberReadHistory;

/**
 * @Title: MemberReadHistoryRepository
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description:
 */
public interface MemberReadHistoryRepository extends MongoRepository<MemberReadHistory,String> {
    Page<MemberReadHistory> findByMemberIdOrderByCreateTimeDesc(Long memberId, Pageable pageable);
    void deleteAllByMemberId(Long memberId);
}
