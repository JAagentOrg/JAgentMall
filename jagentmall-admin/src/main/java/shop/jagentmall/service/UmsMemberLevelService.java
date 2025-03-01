package shop.jagentmall.service;

import shop.jagentmall.model.UmsMemberLevel;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/01
 * @Description: 会员等级管理Service
 */
public interface UmsMemberLevelService {
    /**
     * 获取所有会员登录
     * @param defaultStatus 是否为默认会员
     */
    List<UmsMemberLevel> list(Integer defaultStatus);
}
