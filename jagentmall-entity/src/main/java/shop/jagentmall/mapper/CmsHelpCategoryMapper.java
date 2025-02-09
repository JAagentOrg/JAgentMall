package shop.jagentmall.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.CmsHelpCategory;
import shop.jagentmall.model.CmsHelpCategoryExample;

public interface CmsHelpCategoryMapper {
    long countByExample(CmsHelpCategoryExample example);

    int deleteByExample(CmsHelpCategoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CmsHelpCategory row);

    int insertSelective(CmsHelpCategory row);

    List<CmsHelpCategory> selectByExample(CmsHelpCategoryExample example);

    CmsHelpCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") CmsHelpCategory row, @Param("example") CmsHelpCategoryExample example);

    int updateByExample(@Param("row") CmsHelpCategory row, @Param("example") CmsHelpCategoryExample example);

    int updateByPrimaryKeySelective(CmsHelpCategory row);

    int updateByPrimaryKey(CmsHelpCategory row);
}