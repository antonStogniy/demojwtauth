package com.example.demojwtauth.repository;

import com.example.demojwtauth.entity.Role;
import com.example.demojwtauth.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
