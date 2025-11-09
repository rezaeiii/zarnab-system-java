package com.zarnab.panel.ingot.service;

import com.zarnab.panel.auth.model.User;
import com.zarnab.panel.core.dto.req.PageableRequest;
import com.zarnab.panel.core.dto.res.PageableResponse;
import com.zarnab.panel.ingot.dto.IngotDtos;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotCreateRequest;
import com.zarnab.panel.ingot.dto.IngotDtos.IngotResponse;
import com.zarnab.panel.ingot.dto.req.BatchCreateRequest;
import com.zarnab.panel.ingot.dto.req.BulkIngotStateChangeRequest;
import com.zarnab.panel.ingot.dto.res.BatchCreateResponse;
import com.zarnab.panel.ingot.dto.res.BatchIngotResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IngotService {
    PageableResponse<IngotResponse> list(User requester, PageableRequest pageableRequest);

    IngotResponse create(IngotCreateRequest request);

    IngotResponse inquiry(String serial);

    BatchCreateResponse createBatch(BatchCreateRequest request);

    String getBatchCsv(Long batchId);

    List<BatchIngotResponse> getBatchIngots(Long batchId);

    IngotResponse assignIngot(String serial);

    IngotResponse unassignIngot(String serial);

    void bulkStateChange(BulkIngotStateChangeRequest request);

    PageableResponse<IngotDtos.IngotBatchResponse> listBatches(PageableRequest pageableRequest);
}
