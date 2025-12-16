package com.ssafy.hm.dto;

public class AttractionLine {
	private Integer lineId;
	private Integer attId;

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


	@Override
	public String toString() {
		return "AttractionLine [lineId=" + lineId + ", attId=" + attId + "]";
	}
}
