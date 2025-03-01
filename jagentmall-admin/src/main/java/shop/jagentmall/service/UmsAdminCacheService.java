package shop.jagentmall.service;

import shop.jagentmall.model.UmsAdmin;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/02/28
 * @Description: 后台用户缓存操作类
 */
public interface UmsAdminCacheService {

    /**
     * 获取缓存后台用户信息
     */
    UmsAdmin getAdmin(Long adminId);

    /**
     * 设置缓存后台用户信息
     */
    void setAdmin(UmsAdmin admin);

    /**
     * 删除后台用户缓存
     */
    void delAdmin(Long adminId);

}
