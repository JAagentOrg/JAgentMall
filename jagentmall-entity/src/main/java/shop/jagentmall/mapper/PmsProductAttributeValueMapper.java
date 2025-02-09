package shop.jagentmall.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.PmsProductAttributeValue;
import shop.jagentmall.model.PmsProductAttributeValueExample;

public interface PmsProductAttributeValueMapper {
    long countByExample(PmsProductAttributeValueExample example);

    int deleteByExample(PmsProductAttributeValueExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductAttributeValue row);

    int insertSelective(PmsProductAttributeValue row);

    List<PmsProductAttributeValue> selectByExample(PmsProductAttributeValueExample example);

    PmsProductAttributeValue selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductAttributeValue row, @Param("example") PmsProductAttributeValueExample example);

    int updateByExample(@Param("row") PmsProductAttributeValue row, @Param("example") PmsProductAttributeValueExample example);

    int updateByPrimaryKeySelective(PmsProductAttributeValue row);

    int updateByPrimaryKey(PmsProductAttributeValue row);
}