package shop.jagentmall.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.PmsProductCategory;
import shop.jagentmall.model.PmsProductCategoryExample;

public interface PmsProductCategoryMapper {
    long countByExample(PmsProductCategoryExample example);

    int deleteByExample(PmsProductCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductCategory row);

    int insertSelective(PmsProductCategory row);

    List<PmsProductCategory> selectByExampleWithBLOBs(PmsProductCategoryExample example);

    List<PmsProductCategory> selectByExample(PmsProductCategoryExample example);

    PmsProductCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductCategory row, @Param("example") PmsProductCategoryExample example);

    int updateByExampleWithBLOBs(@Param("row") PmsProductCategory row, @Param("example") PmsProductCategoryExample example);

    int updateByExample(@Param("row") PmsProductCategory row, @Param("example") PmsProductCategoryExample example);

    int updateByPrimaryKeySelective(PmsProductCategory row);

    int updateByPrimaryKeyWithBLOBs(PmsProductCategory row);

    int updateByPrimaryKey(PmsProductCategory row);
}