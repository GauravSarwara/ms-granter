package com.granter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.granter.entity.GranterApplication;
@Repository
public interface GranterApplicationRepository extends JpaRepository<GranterApplication, Long> {

    // 🔹 Find all applications of a user
    List<GranterApplication> findByUserId(Long userId);

    // 🔹 Find by status
    List<GranterApplication> findByStatus(Boolean status);

}