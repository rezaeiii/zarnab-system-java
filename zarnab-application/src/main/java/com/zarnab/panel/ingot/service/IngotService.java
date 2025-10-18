package com.zarnab.panel.ingot.service;

import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.dto.req.BatchCreateRequest;
import com.zarnab.panel.ingot.dto.res.BatchCreateResponse;
import com.zarnab.panel.ingot.dto.res.IngotBatchResponse;

import java.util.List;

public interface IngotService {
	List<IngotResponse> list(User requester);
	IngotResponse create(IngotCreateRequest request);
	IngotResponse inquiry(String serial);

	BatchCreateResponse createBatch(BatchCreateRequest request);

	String getBatchCsv(Long batchId);

	IngotResponse assignIngot(String serial);

	List<IngotBatchResponse> listBatches();
} 