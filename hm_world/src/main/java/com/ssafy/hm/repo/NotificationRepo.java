package com.ssafy.hm.repo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.hm.dto.PushNotification;

@Mapper
public interface NotificationRepo {
    int insertScheduled(PushNotification notification);

    int insertRepeat(PushNotification notification);

    List<PushNotification> selectScheduled();

    List<PushNotification> selectRepeat();

    List<PushNotification> selectDueScheduled(@Param("now") String now);

    List<PushNotification> selectActiveRepeats();

    PushNotification selectById(@Param("id") int id);

    int updateScheduled(PushNotification notification);

    int updateRepeat(PushNotification notification);

    int markScheduledSent(@Param("id") int id, @Param("sentAt") String sentAt);

    int updateRepeatLastSent(@Param("id") int id, @Param("sentAt") String sentAt);

    int delete(@Param("id") int id);
}
