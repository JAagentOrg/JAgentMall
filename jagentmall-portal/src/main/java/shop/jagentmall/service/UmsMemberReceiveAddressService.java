package shop.jagentmall.service;

import org.springframework.transaction.annotation.Transactional;
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

    /**
     * 添加收货地址
     * @param address
     * @return
     */
    @Transactional
    int add(UmsMemberReceiveAddress address);

    /**
     * 删除收货地址
     * @param id
     * @return
     */
    @Transactional
    int delete(Long id);

    /**
     * 修改收货地址
     * @param id
     * @param address
     * @return
     */
    @Transactional
    int update(Long id, UmsMemberReceiveAddress address);
}
