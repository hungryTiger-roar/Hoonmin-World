package com.ssafy.hm.service;

import java.util.List;

import com.ssafy.hm.dto.AttractionLine;
import com.ssafy.hm.dto.FriendLine;

public interface LineService {
	boolean joinLine(AttractionLine line);
	boolean exitLine(Integer lineId);
	List<AttractionLine> getLines(Integer attId);

	boolean addFriendToLine(FriendLine friendLine);
	boolean removeFriendFromLine(Integer friendLineId);
	List<FriendLine> getFriendLines(Integer lineId);
}
