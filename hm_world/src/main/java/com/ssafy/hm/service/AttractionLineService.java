package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.AttractionLine;
import com.ssafy.hm.dto.AttractionLineCreateRequest;
import com.ssafy.hm.dto.AttractionLineMember;

public interface AttractionLineService {
    Integer createGroupLine(AttractionLineCreateRequest request);
    void leaveGroup(Integer lineId, String userId);
    void deleteLine(Integer lineId);
    List<AttractionLineMember> getMembers(Integer lineId);
    AttractionLineMember getMember(Integer lineId, String userId);
}
