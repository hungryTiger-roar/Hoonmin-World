package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.hm.dto.AttractionLineCreateRequest;
import com.ssafy.hm.dto.AttractionLineMember;
import com.ssafy.hm.dto.AttractionLine;
import com.ssafy.hm.service.AttractionLineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/line")
@Tag(name = "어트랙션 줄서기", description = "어트랙션 줄서기 관리")
public class AttractionLineController {

    private final AttractionLineService lineService;

    public AttractionLineController(AttractionLineService lineService) {
        this.lineService = lineService;
    }

    // 그룹 줄서기
    @PostMapping("/{attId}")
    @Operation(summary = "그룹 줄서기 생성")
    public ResponseEntity<?> createGroupLine(@PathVariable Integer attId, @RequestBody AttractionLineCreateRequest request) {
        request.setAttId(attId);
        Integer lineId = lineService.createGroupLine(request);
        return ResponseEntity.ok(lineId);
    }

    @DeleteMapping("/{lineId}/member/{userId}")
    @Operation(summary = "그룹 줄서기 탈퇴")
    public ResponseEntity<?> leaveGroup(@PathVariable Integer lineId, @PathVariable String userId) {

        lineService.leaveGroup(lineId, userId);
        return ResponseEntity.ok("탈퇴 완료");
    }

    @GetMapping("/{lineId}/members")
    @Operation(summary = "줄의 모든 멤버 조회")
    public ResponseEntity<List<AttractionLineMember>> getMembers(@PathVariable Integer lineId) {
        List<AttractionLineMember> members = lineService.getMembers(lineId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/attraction/{attId}")
    @Operation(summary = "어트랙션별 줄서기목록 조회")
    public ResponseEntity<List<AttractionLine>> getLinesByAttraction(@PathVariable Integer attId) {
        return ResponseEntity.ok(lineService.getLinesByAttraction(attId));
    }

    @GetMapping("/{lineId}/member/{userId}")
    @Operation(summary = "줄의 특정 멤버 단건 조회")
    public ResponseEntity<AttractionLineMember> getMember(@PathVariable Integer lineId, @PathVariable String userId) {
        AttractionLineMember member = lineService.getMember(lineId, userId);
        return member != null ? ResponseEntity.ok(member) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{lineId}")
    @Operation(summary = "줄 전체 삭제")
    public ResponseEntity<Void> deleteLine(@PathVariable Integer lineId) {
        lineService.deleteLine(lineId);
        return ResponseEntity.noContent().build();
    }
}
