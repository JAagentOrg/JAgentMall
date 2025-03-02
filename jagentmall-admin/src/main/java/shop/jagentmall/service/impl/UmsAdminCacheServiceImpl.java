package shop.jagentmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shop.jagentmall.model.UmsAdmin;
import shop.jagentmall.service.RedisService;
import shop.jagentmall.service.UmsAdminCacheService;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/28
 * @Description: UmsAdminCacheService实现类
 */
@Service
public class UmsAdminCacheServiceImpl implements UmsAdminCacheService {
    @Autowired
    private RedisService redisService;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;
    @Value("${redis.key.admin}")
    private String REDIS_KEY_ADMIN;

    @Override
    public UmsAdmin getAdmin(Long adminId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + adminId;
        return (UmsAdmin) redisService.get(key);
    }

    @Override
    public void setAdmin(UmsAdmin admin) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + admin.getId();
        redisService.set(key, admin, REDIS_EXPIRE);
    }

    @Override
    public void delAdmin(Long adminId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ADMIN + ":" + adminId;
        redisService.del(key);
    }

}
