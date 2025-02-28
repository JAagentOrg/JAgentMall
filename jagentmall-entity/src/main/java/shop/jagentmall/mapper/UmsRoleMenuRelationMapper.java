package shop.jagentmall.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.UmsRoleMenuRelation;
import shop.jagentmall.model.UmsRoleMenuRelationExample;

public interface UmsRoleMenuRelationMapper {
    long countByExample(UmsRoleMenuRelationExample example);

    int deleteByExample(UmsRoleMenuRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsRoleMenuRelation row);

    int insertSelective(UmsRoleMenuRelation row);

    List<UmsRoleMenuRelation> selectByExample(UmsRoleMenuRelationExample example);

    UmsRoleMenuRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsRoleMenuRelation row, @Param("example") UmsRoleMenuRelationExample example);

    int updateByExample(@Param("row") UmsRoleMenuRelation row, @Param("example") UmsRoleMenuRelationExample example);

    int updateByPrimaryKeySelective(UmsRoleMenuRelation row);

    int updateByPrimaryKey(UmsRoleMenuRelation row);
}