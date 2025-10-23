package com.zarnab.panel.core.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public PageableResponse(List<T> responses, long totalElements, int number, int size) {
        this.content = responses;
        this.totalElements = totalElements;
        this.pageNumber = number;
        this.pageSize = size;
        this.totalPages = (int) Math.ceil(totalElements / (double) size);
    }
}
