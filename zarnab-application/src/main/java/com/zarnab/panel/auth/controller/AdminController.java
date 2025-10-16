package com.zarnab.panel.auth.controller;

import com.zarnab.panel.auth.dto.PageDto;
import com.zarnab.panel.auth.dto.req.ToggleRoleRequest;
import com.zarnab.panel.auth.service.PageRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PageRoleService pageRoleService;

    @GetMapping("/pages")
    public List<PageDto> getPages() {
        return pageRoleService.getPages();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pages/toggle-role")
    public ResponseEntity<Void> toggleRole(@Valid @RequestBody ToggleRoleRequest request) {
        pageRoleService.toggleRoleToPage(request.path(), request.method(), request.roleName());
        return ResponseEntity.ok().build();
    }
}
