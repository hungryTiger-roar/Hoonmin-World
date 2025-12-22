package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.hm.dto.FcmTokenRequest;

@Mapper
public interface FcmTokenRepo {
    int upsert(FcmTokenRequest token);

    List<String> selectTokensByUserIds(@Param("userIds") List<String> userIds);

    List<String> selectTokensByTicketTrue();

    List<String> selectTokensByUserIdsAndTicketTrue(@Param("userIds") List<String> userIds);
}
