package shop.jagentmall.service;

/**
 * @Title: NotificationService
 * @Author: [tianyou]
 * @Date: 2025/2/28
 * @Description: 支付定时任务通知service
 */
public interface NotificationService {
    void notifyUser(String userId, String msg);
}
