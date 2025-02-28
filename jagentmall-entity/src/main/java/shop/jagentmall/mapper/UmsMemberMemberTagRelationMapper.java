package shop.jagentmall.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.UmsMemberMemberTagRelation;
import shop.jagentmall.model.UmsMemberMemberTagRelationExample;

public interface UmsMemberMemberTagRelationMapper {
    long countByExample(UmsMemberMemberTagRelationExample example);

    int deleteByExample(UmsMemberMemberTagRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberMemberTagRelation row);

    int insertSelective(UmsMemberMemberTagRelation row);

    List<UmsMemberMemberTagRelation> selectByExample(UmsMemberMemberTagRelationExample example);

    UmsMemberMemberTagRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsMemberMemberTagRelation row, @Param("example") UmsMemberMemberTagRelationExample example);

    int updateByExample(@Param("row") UmsMemberMemberTagRelation row, @Param("example") UmsMemberMemberTagRelationExample example);

    int updateByPrimaryKeySelective(UmsMemberMemberTagRelation row);

    int updateByPrimaryKey(UmsMemberMemberTagRelation row);
}