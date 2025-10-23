package com.zarnab.panel.ingot.service;

import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.ingot.dto.req.BatchCreateRequest;
import com.zarnab.panel.ingot.dto.res.BatchCreateResponse;
import com.zarnab.panel.ingot.dto.res.BatchIngotResponse;
import com.zarnab.panel.ingot.dto.res.IngotBatchResponse;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IngotService {
	PageableResponse<IngotResponse> list(User requester, PageableRequest pageableRequest);
	IngotResponse create(IngotCreateRequest request);
	IngotResponse inquiry(String serial);

	BatchCreateResponse createBatch(BatchCreateRequest request);

	String getBatchCsv(Long batchId);

	@Transactional(readOnly = true)
	List<BatchIngotResponse> getBatchIngots(Long batchId);

	IngotResponse assignIngot(String serial);

	List<IngotBatchResponse> listBatches();
} 