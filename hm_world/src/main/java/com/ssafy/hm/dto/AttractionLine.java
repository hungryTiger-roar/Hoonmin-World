package com.ssafy.hm.dto;

import java.util.List;

public class AttractionLine {
	private Integer lineId;
	private Integer attId;
	private List<AttractionLineMember> members;

	public AttractionLine() {
	}

	public AttractionLine(Integer lineId, Integer attId) {
		this.lineId = lineId;
		this.attId = attId;
	}

	public Integer getLineId() {
		return lineId;
	}

	public void setLineId(Integer lineId) {
		this.lineId = lineId;
	}

	public Integer getAttId() {
		return attId;
	}

	public void setAttId(Integer attId) {
		this.attId = attId;
	}

	public List<AttractionLineMember> getMembers() {
		return members;
	}

	public void setMembers(List<AttractionLineMember> members) {
		this.members = members;
	}


	@Override
	public String toString() {
		return "AttractionLine [lineId=" + lineId + ", attId=" + attId + ", members=" + members + "]";
	}
}
