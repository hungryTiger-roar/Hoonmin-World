package com.ssafy.hm.dto;

public class AttractionLineMember {
    private Integer id;
    private Integer lineId;
    private String userId;

    public AttractionLineMember() {}

    public AttractionLineMember(Integer lineId, String userId) {
        this.lineId = lineId;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AttractionLineMember [id=" + id + ", lineId=" + lineId + ", userId=" + userId + "]";
    }
}
