package shop.jagentmall.service;

import shop.jagentmall.model.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @Title: UmsMemberReceiveAddressService
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 用户收货地址管理service
 */
public interface UmsMemberReceiveAddressService {
    /**
     * 获取用户的收件地址
     * @return
     */
    List<UmsMemberReceiveAddress> list();

    /**
     * 获取对应id的收件地址
     * @param memberReceiveAddressId
     * @return
     */
    UmsMemberReceiveAddress getItem(Long memberReceiveAddressId);
}
