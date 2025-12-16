package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.hm.dto.AttractionLineMember;

@Mapper
public interface AttractionLineMemberRepo {
    int insert(AttractionLineMember member);
    int insertAll(List<AttractionLineMember> members);
    List<AttractionLineMember> selectByLineId(Integer lineId);
    AttractionLineMember selectByLineIdAndUserId(@Param("lineId") Integer lineId, @Param("userId") String userId);
    int deleteByLineId(Integer lineId);
    
    // [수정] @Param 추가
    int deleteMember(@Param("lineId") Integer lineId, @Param("userId") String userId);

    // OK
    int countMembers(@Param("lineId") Integer lineId); // [수정] Param 통일
}
