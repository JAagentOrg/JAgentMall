package shop.jagentmall.service;

import java.util.Map;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/02
 * @Description: 后台资源管理Service
 */
public interface UmsResourceService {

    /**
     * 初始化路径与资源访问规则
     */
    Map<String,String> initPathResourceMap();

}
