package shop.jagentmall.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.jagentmall.service.NotificationService;

/**
 * @Title: NotificationService
 * @Author: [tianyou]
 * @Date: 2025/2/28
 * @Description:
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {
    @Override
    public void notifyUser(String userId, String msg) {
        log.info("通知user:{},msg:{}", userId, msg);
        // TODO 使用推送和短信提示的方式
    }
}
