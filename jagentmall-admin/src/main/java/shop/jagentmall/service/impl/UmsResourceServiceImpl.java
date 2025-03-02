package shop.jagentmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shop.jagentmall.constant.AuthConstant;
import shop.jagentmall.mapper.UmsResourceMapper;
import shop.jagentmall.model.UmsResource;
import shop.jagentmall.model.UmsResourceExample;
import shop.jagentmall.service.RedisService;
import shop.jagentmall.service.UmsResourceService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/02
 * @Description:
 */
@Service
public class UmsResourceServiceImpl implements UmsResourceService {

    @Autowired
    private UmsResourceMapper resourceMapper;
    @Autowired
    private RedisService redisService;
    @Value("${spring.application.name}")
    private String applicationName;


    @Override
    public Map<String,String> initPathResourceMap() {
        Map<String,String> pathResourceMap = new TreeMap<>();
        List<UmsResource> resourceList = resourceMapper.selectByExample(new UmsResourceExample());
        for (UmsResource resource : resourceList) {
            pathResourceMap.put("/"+applicationName+resource.getUrl(),resource.getId()+":"+resource.getName());
        }
        redisService.del(AuthConstant.PATH_RESOURCE_MAP);
        redisService.hSetAll(AuthConstant.PATH_RESOURCE_MAP, pathResourceMap);
        return pathResourceMap;

    }
}
