package shop.jagentmall.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.UmsMemberLoginLog;
import shop.jagentmall.model.UmsMemberLoginLogExample;

public interface UmsMemberLoginLogMapper {
    long countByExample(UmsMemberLoginLogExample example);

    int deleteByExample(UmsMemberLoginLogExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberLoginLog row);

    int insertSelective(UmsMemberLoginLog row);

    List<UmsMemberLoginLog> selectByExample(UmsMemberLoginLogExample example);

    UmsMemberLoginLog selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsMemberLoginLog row, @Param("example") UmsMemberLoginLogExample example);

    int updateByExample(@Param("row") UmsMemberLoginLog row, @Param("example") UmsMemberLoginLogExample example);

    int updateByPrimaryKeySelective(UmsMemberLoginLog row);

    int updateByPrimaryKey(UmsMemberLoginLog row);
}