package shop.jagentmall.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.OmsOrderReturnReason;
import shop.jagentmall.model.OmsOrderReturnReasonExample;

public interface OmsOrderReturnReasonMapper {
    long countByExample(OmsOrderReturnReasonExample example);

    int deleteByExample(OmsOrderReturnReasonExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderReturnReason row);

    int insertSelective(OmsOrderReturnReason row);

    List<OmsOrderReturnReason> selectByExample(OmsOrderReturnReasonExample example);

    OmsOrderReturnReason selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsOrderReturnReason row, @Param("example") OmsOrderReturnReasonExample example);

    int updateByExample(@Param("row") OmsOrderReturnReason row, @Param("example") OmsOrderReturnReasonExample example);

    int updateByPrimaryKeySelective(OmsOrderReturnReason row);

    int updateByPrimaryKey(OmsOrderReturnReason row);
}