package com.ssafy.hm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.hm.dto.AttractionLine;
import com.ssafy.hm.dto.FriendLine;
import com.ssafy.hm.repo.AttractionLineRepo;
import com.ssafy.hm.repo.FriendLineRepo;

@Service
public class LineServiceImpl implements LineService {

	private final AttractionLineRepo attractionLineRepo;
	private final FriendLineRepo friendLineRepo;

	public LineServiceImpl(AttractionLineRepo attractionLineRepo, FriendLineRepo friendLineRepo) {
		this.attractionLineRepo = attractionLineRepo;
		this.friendLineRepo = friendLineRepo;
	}

	@Override
	@Transactional
	public boolean joinLine(AttractionLine line) {
		return attractionLineRepo.insert(line) == 1;
	}

	@Override
	@Transactional
	public boolean exitLine(Integer lineId) {
		return attractionLineRepo.delete(lineId) == 1;
	}

	@Override
	public List<AttractionLine> getLines(Integer attId) {
		return attractionLineRepo.selectByAttraction(attId);
	}

	@Override
	@Transactional
	public boolean addFriendToLine(FriendLine friendLine) {
		return friendLineRepo.insert(friendLine) == 1;
	}

	@Override
	@Transactional
	public boolean removeFriendFromLine(Integer friendLineId) {
		return friendLineRepo.delete(friendLineId) == 1;
	}

	@Override
	public List<FriendLine> getFriendLines(Integer lineId) {
		return friendLineRepo.selectByLine(lineId);
	}
}
