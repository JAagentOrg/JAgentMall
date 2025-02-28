package shop.jagentmall.service.impl;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.jagentmall.api.CommonPage;
import shop.jagentmall.dao.HomeDao;
import shop.jagentmall.mapper.PmsBrandMapper;
import shop.jagentmall.mapper.PmsProductMapper;
import shop.jagentmall.model.PmsBrand;
import shop.jagentmall.model.PmsProduct;
import shop.jagentmall.model.PmsProductExample;
import shop.jagentmall.service.PortalBrandService;

import java.util.List;

/**
 * @Title: PortalBrandServiceImpl
 * @Author: [tianyou]
 * @Date: 2025/2/24
 * @Description: 前台品牌管理Service实现类
 */
@Service
public class PortalBrandServiceImpl implements PortalBrandService {
    @Autowired
    private HomeDao homeDao;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> recommendList(Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return homeDao.getRecommendBrandList(offset, pageSize);
    }

    @Override
    public PmsBrand detail(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Override
    public CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andDeleteStatusEqualTo(0)
                .andBrandIdEqualTo(brandId);
        List<PmsProduct> productList = productMapper.selectByExample(example);
        return CommonPage.restPage(productList);
    }
}
