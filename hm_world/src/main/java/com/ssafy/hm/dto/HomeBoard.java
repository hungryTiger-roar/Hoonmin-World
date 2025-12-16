package com.ssafy.hm.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class HomeBoard {
	private Integer boardId;
	private String boardTitle;
	private String boardContent;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime boardTime;

	public HomeBoard(Integer boardId, String boardTitle, String boardContent, LocalDateTime boardTime) {
		this.boardId = boardId;
		this.boardTitle = boardTitle;
		this.boardContent = boardContent;
		this.boardTime = boardTime;
	}

	public Integer getBoardId() {
		return boardId;
	}

	public void setBoardId(Integer boardId) {
		this.boardId = boardId;
	}

	public String getBoardTitle() {
		return boardTitle;
	}

	public void setBoardTitle(String boardTitle) {
		this.boardTitle = boardTitle;
	}

	public String getBoardContent() {
		return boardContent;
	}

	public void setBoardContent(String boardContent) {
		this.boardContent = boardContent;
	}

	public LocalDateTime getBoardTime() {
		return boardTime;
	}

	public void setBoardTime(LocalDateTime boardTime) {
		this.boardTime = boardTime;
	}

	@Override
	public String toString() {
		return "HomeBoard [boardId=" + boardId + ", boardTitle=" + boardTitle + ", boardContent=" + boardContent
				+ ", boardTime=" + boardTime + "]";
	}	
	
}
