package com.ssafy.hm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.AttractionLine;
import com.ssafy.hm.dto.AttractionLineCreateRequest;
import com.ssafy.hm.dto.AttractionLineMember;
import com.ssafy.hm.repo.AccountRepo;
import com.ssafy.hm.repo.AttractionLineRepo;
import com.ssafy.hm.repo.AttractionLineMemberRepo;

@Service
public class AttractionLineServiceImpl implements AttractionLineService {

    private final AttractionLineRepo lineRepo;
    private final AttractionLineMemberRepo memberRepo;
    private final AccountRepo accountRepo;

    public AttractionLineServiceImpl(AttractionLineRepo lineRepo,
                                     AttractionLineMemberRepo memberRepo,
                                     AccountRepo accountRepo) {
        this.lineRepo = lineRepo;
        this.memberRepo = memberRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    @Transactional
    public Integer createGroupLine(AttractionLineCreateRequest request) {
        // 1. 줄(그룹) 생성
        AttractionLine line = new AttractionLine();
        line.setAttId(request.getAttId());
        lineRepo.insert(line); // lineId 생성됨

        // 2. 그룹 멤버 생성
        List<String> userIds = request.getUserIds();
        if (userIds == null) {
            userIds = new ArrayList<>();
        }
        List<AttractionLineMember> members = new ArrayList<>();
        for (String userId : userIds) {
            members.add(new AttractionLineMember(line.getLineId(), userId));
            accountRepo.updateAttId(userId, request.getAttId());
        }
        if (!members.isEmpty()) {
            memberRepo.insertAll(members);
        }

        return line.getLineId();
    }
    
    @Override
    @Transactional
    public void leaveGroup(Integer lineId, String userId) {
        memberRepo.deleteMember(lineId, userId);
        accountRepo.updateAttId(userId, null);
        int remain = memberRepo.countMembers(lineId);

        if (remain == 0) {
            lineRepo.delete(lineId); // AttractionLineRepo에 deleteLine 필요
        }
    }

    @Override
    @Transactional
    public void deleteLine(Integer lineId) {
        // 줄 전체 삭제 시 멤버들의 att_id를 초기화하고 줄/멤버 데이터를 제거
        List<AttractionLineMember> members = memberRepo.selectByLineId(lineId);
        for (AttractionLineMember member : members) {
            accountRepo.updateAttId(member.getUserId(), null);
        }
        memberRepo.deleteByLineId(lineId);
        lineRepo.delete(lineId);
    }

    @Override
    public List<AttractionLine> getLinesByAttraction(Integer attId) {
        List<AttractionLine> lines = lineRepo.selectByAttId(attId);
        for (AttractionLine line : lines) {
            line.setMembers(memberRepo.selectByLineId(line.getLineId()));
        }
        return lines;
    }

    @Override
    public List<AttractionLineMember> getMembers(Integer lineId) {
        return memberRepo.selectByLineId(lineId);
    }

    @Override
    public AttractionLineMember getMember(Integer lineId, String userId) {
        return memberRepo.selectByLineIdAndUserId(lineId, userId);
    }
}
