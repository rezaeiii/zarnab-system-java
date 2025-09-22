package com.zarnab.panel.ingot.service;

import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.auth.model.User;

import java.util.List;

public interface IngotService {
	List<IngotResponse> list(User requester);
	IngotResponse create(IngotCreateRequest request);
} 