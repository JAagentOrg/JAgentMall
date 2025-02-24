package shop.jagentmall.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Title: HomeFlashPromotion
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 当前秒杀信息
 */
@Data
public class HomeFlashPromotion {
    private Date startTime;
    private Date endTime;
    private Date nextStartTime;
    private Date nextEndTime;
    //属于该秒杀活动的商品
    private List<FlashPromotionProduct> productList;
}
