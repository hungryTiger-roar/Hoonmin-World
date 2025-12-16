package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createGroupLine(@PathVariable Integer attId, @RequestBody List<String> userIds) {

        Integer lineId = lineService.createGroupLine(attId, userIds);
        return ResponseEntity.ok(lineId);
    }
    
    @DeleteMapping("/{lineId}/member/{userId}")
    @Operation(summary = "그룹 줄서기 탈퇴")
    public ResponseEntity<?> leaveGroup(@PathVariable Integer lineId, @PathVariable String userId) {

        lineService.leaveGroup(lineId, userId);
        return ResponseEntity.ok("탈퇴 완료");
    }
}
