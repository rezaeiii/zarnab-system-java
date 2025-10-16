package com.zarnab.panel.auth.service;

import com.zarnab.panel.auth.dto.PageDto;

import java.util.List;

public interface PageRoleService {

    List<PageDto> getPages();

    void toggleRoleToPage(String path, String method, String roleName);
}
