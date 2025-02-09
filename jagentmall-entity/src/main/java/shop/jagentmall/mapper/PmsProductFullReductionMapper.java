package shop.jagentmall.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.PmsProductFullReduction;
import shop.jagentmall.model.PmsProductFullReductionExample;

public interface PmsProductFullReductionMapper {
    long countByExample(PmsProductFullReductionExample example);

    int deleteByExample(PmsProductFullReductionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductFullReduction row);

    int insertSelective(PmsProductFullReduction row);

    List<PmsProductFullReduction> selectByExample(PmsProductFullReductionExample example);

    PmsProductFullReduction selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductFullReduction row, @Param("example") PmsProductFullReductionExample example);

    int updateByExample(@Param("row") PmsProductFullReduction row, @Param("example") PmsProductFullReductionExample example);

    int updateByPrimaryKeySelective(PmsProductFullReduction row);

    int updateByPrimaryKey(PmsProductFullReduction row);
}