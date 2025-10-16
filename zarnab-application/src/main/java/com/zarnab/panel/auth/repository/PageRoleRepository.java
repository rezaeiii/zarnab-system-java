package com.zarnab.panel.auth.repository;

import com.zarnab.panel.auth.model.PageRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PageRoleRepository extends JpaRepository<PageRole, Long> {

    Optional<PageRole> findByPathAndMethod(String path, String method);
}
