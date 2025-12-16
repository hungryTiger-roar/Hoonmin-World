package com.ssafy.hm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.hm.dto.HomeBoard;
import com.ssafy.hm.service.HomeBoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/board")
@Tag(name = "공지사항", description = "공지사항 관리")
public class HomeBoardController {

    private final HomeBoardService homeBoardService;

    public HomeBoardController(HomeBoardService homeBoardService) {
        this.homeBoardService = homeBoardService;
    }

    @GetMapping
    @Operation(summary = "홈 게시판 전체 조회")
    public ResponseEntity<List<HomeBoard>> list() {
        return ResponseEntity.ok(homeBoardService.getBoards());
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "홈 게시판 단건 조회")
    public ResponseEntity<HomeBoard> detail(@PathVariable int boardId) {
        return ResponseEntity.ok(homeBoardService.getBoard(boardId));
    }

    @PostMapping
    @Operation(summary = "홈 게시판 글 등록")
    public ResponseEntity<Void> write(@RequestBody HomeBoard board) {
        return homeBoardService.writeBoard(board)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{boardId}")
    @Operation(summary = "홈 게시판 글 삭제")
    public ResponseEntity<Void> delete(@PathVariable int boardId) {
        return homeBoardService.removeBoard(boardId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
