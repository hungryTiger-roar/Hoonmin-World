package com.ssafy.hm.service;

import java.util.List;

public interface AttractionLineService {
    Integer createGroupLine(Integer attId, List<String> userIds);
    void leaveGroup(Integer lineId, String userId);
}