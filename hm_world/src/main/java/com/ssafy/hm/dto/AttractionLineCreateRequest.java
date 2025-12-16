package com.ssafy.hm.dto;

import java.util.List;

public class AttractionLineCreateRequest {
    private Integer attId;
    private List<String> userIds;

    public Integer getAttId() {
        return attId;
    }

    public void setAttId(Integer attId) {
        this.attId = attId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
