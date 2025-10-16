package com.zarnab.panel.auth.service;

import com.zarnab.panel.auth.dto.PageDto;
import com.zarnab.panel.auth.model.PageRole;
import com.zarnab.panel.auth.model.Role;
import com.zarnab.panel.auth.repository.PageRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageRoleServiceImpl implements PageRoleService {

    private final PageRoleRepository pageRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PageDto> getPages() {
        return pageRoleRepository.findAll().stream()
                .map(pageRole -> new PageDto(
                        pageRole.getPath(),
                        pageRole.getMethod(),
                        pageRole.getRoles().stream().map(Role::name).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleRoleToPage(String path, String method, String roleName) {
        Role roleToToggle = Role.valueOf(roleName.toUpperCase());

        PageRole pageRole = pageRoleRepository.findByPathAndMethod(path, method)
                .orElseGet(() -> PageRole.builder()
                        .path(path)
                        .method(method)
                        .roles(new HashSet<>())
                        .build());

        Set<Role> roles = pageRole.getRoles();
        if (roles.contains(roleToToggle)) {
            roles.remove(roleToToggle);
        } else {
            roles.add(roleToToggle);
        }

        if (roles.isEmpty() && pageRole.getId() != null) {
            pageRoleRepository.delete(pageRole);
        } else {
            pageRoleRepository.save(pageRole);
        }
    }
}
