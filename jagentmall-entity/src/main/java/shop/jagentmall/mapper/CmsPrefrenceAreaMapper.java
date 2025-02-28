package shop.jagentmall.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.CmsPrefrenceArea;
import shop.jagentmall.model.CmsPrefrenceAreaExample;

public interface CmsPrefrenceAreaMapper {
    long countByExample(CmsPrefrenceAreaExample example);

    int deleteByExample(CmsPrefrenceAreaExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsPrefrenceArea row);

    int insertSelective(CmsPrefrenceArea row);

    List<CmsPrefrenceArea> selectByExampleWithBLOBs(CmsPrefrenceAreaExample example);

    List<CmsPrefrenceArea> selectByExample(CmsPrefrenceAreaExample example);

    CmsPrefrenceArea selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsPrefrenceArea row, @Param("example") CmsPrefrenceAreaExample example);

    int updateByExampleWithBLOBs(@Param("row") CmsPrefrenceArea row, @Param("example") CmsPrefrenceAreaExample example);

    int updateByExample(@Param("row") CmsPrefrenceArea row, @Param("example") CmsPrefrenceAreaExample example);

    int updateByPrimaryKeySelective(CmsPrefrenceArea row);

    int updateByPrimaryKeyWithBLOBs(CmsPrefrenceArea row);

    int updateByPrimaryKey(CmsPrefrenceArea row);
}