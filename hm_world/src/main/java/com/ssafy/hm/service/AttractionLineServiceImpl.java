package com.ssafy.hm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.AttractionLine;
import com.ssafy.hm.dto.AttractionLineMember;
import com.ssafy.hm.repo.AttractionLineRepo;
import com.ssafy.hm.repo.AttractionLineMemberRepo;

@Service
public class AttractionLineServiceImpl implements AttractionLineService {

    private final AttractionLineRepo lineRepo;
    private final AttractionLineMemberRepo memberRepo;

    public AttractionLineServiceImpl(AttractionLineRepo lineRepo,
                                     AttractionLineMemberRepo memberRepo) {
        this.lineRepo = lineRepo;
        this.memberRepo = memberRepo;
    }

    @Override
    @Transactional
    public Integer createGroupLine(Integer attId, List<String> userIds) {
        // 1. 줄(그룹) 생성
        AttractionLine line = new AttractionLine();
        line.setAttId(attId);
        lineRepo.insert(line); // lineId 생성됨

        // 2. 그룹 멤버 생성
        List<AttractionLineMember> members = new ArrayList<>();
        for (String userId : userIds) {
            members.add(new AttractionLineMember(line.getLineId(), userId));
        }
        memberRepo.insertAll(members);

        return line.getLineId();
    }
    
    @Override
    @Transactional
    public void leaveGroup(Integer lineId, String userId) {
        memberRepo.deleteMember(lineId, userId);
        int remain = memberRepo.countMembers(lineId);

        if (remain == 0) {
            lineRepo.delete(lineId); // AttractionLineRepo에 deleteLine 필요
        }
    }
}
