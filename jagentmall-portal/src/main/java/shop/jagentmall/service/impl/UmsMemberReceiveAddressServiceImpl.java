package shop.jagentmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import shop.jagentmall.mapper.UmsMemberReceiveAddressMapper;
import shop.jagentmall.model.UmsMember;
import shop.jagentmall.model.UmsMemberReceiveAddress;
import shop.jagentmall.model.UmsMemberReceiveAddressExample;
import shop.jagentmall.service.UmsMemberReceiveAddressService;
import shop.jagentmall.service.UmsMemberService;

import java.util.List;

/**
 * @Title: UmsMemberReceiveAddressServiceImpl
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 用户收货管理service实现类
 */
@Service
public class UmsMemberReceiveAddressServiceImpl implements UmsMemberReceiveAddressService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private UmsMemberReceiveAddressMapper addressMapper;
    @Override
    public List<UmsMemberReceiveAddress> list() {
        UmsMember currentMember = memberService.getCurrentMember();
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(currentMember.getId());
        return addressMapper.selectByExample(example);
    }

    @Override
    public UmsMemberReceiveAddress getItem(Long memberReceiveAddressId) {
        UmsMember currentMember = memberService.getCurrentMember();
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(currentMember.getId()).andIdEqualTo(memberReceiveAddressId);
        List<UmsMemberReceiveAddress> addressList = addressMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(addressList)){
            return addressList.get(0);
        }
        return null;
    }
}
