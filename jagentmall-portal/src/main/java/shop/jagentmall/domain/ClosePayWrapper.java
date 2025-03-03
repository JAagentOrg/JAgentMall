package shop.jagentmall.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Title: ClosePayWarpper
 * @Author: [tianyou]
 * @Date: 2025/3/1
 * @Description: 关闭订单参数封装类
 */
@Data
public class ClosePayWrapper{
    /**
     * 订单号
     */
    private String outTradeNo;
    /**
     * 时间戳
     */
    private String timeOut;
}
