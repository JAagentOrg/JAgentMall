package shop.jagentmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shop.jagentmall.annotation.CacheException;
import shop.jagentmall.model.UmsMember;
import shop.jagentmall.service.RedisService;
import shop.jagentmall.service.UmsMemberCacheService;

/**
 * @Title: UmsMemberCacheServiceImpl
 * @Author: [tianyou]
 * @Date: 2025/2/16
 * @Description: 会员缓存相关service实现类
 */
@Service
public class UmsMemberCacheServiceImpl implements UmsMemberCacheService {
    @Autowired
    private RedisService redisService;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;
    @Value("${redis.expire.authCode}")
    private Long REDIS_EXPIRE_AUTH_CODE;
    @Value("${redis.key.member}")
    private String REDIS_KEY_MEMBER;
    @Value("${redis.key.authCode}")
    private String REDIS_KEY_AUTH_CODE;

    @Override
    public void delMember(Long memberId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_MEMBER + ":" + memberId;
        redisService.del(key);
    }

    @Override
    public UmsMember getMember(Long memberId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_MEMBER + ":" + memberId;
        return (UmsMember) redisService.get(key);
    }

    @Override
    public void setMember(UmsMember member) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_MEMBER + ":" + member.getId();
        redisService.set(key, member, REDIS_EXPIRE);
    }

    @CacheException
    @Override
    public void setAuthCode(String telephone, String authCode) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_AUTH_CODE + ":" + telephone;
        redisService.set(key, authCode, REDIS_EXPIRE_AUTH_CODE);
    }
    @CacheException
    @Override
    public String getAuthCode(String telephone) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_AUTH_CODE + ":" + telephone;
        return (String) redisService.get(key);
    }

    @Override
    public boolean removeAuthCode(String telephone) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_AUTH_CODE + ":" + telephone;
        return redisService.del(key);
    }
}
