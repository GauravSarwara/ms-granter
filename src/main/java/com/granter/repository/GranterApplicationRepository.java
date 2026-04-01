package com.granter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.granter.entity.GranterApplication;
import com.granter.entity.User;

public interface GranterApplicationRepository extends JpaRepository<GranterApplication, Long> {

    // 🔹 Find all applications of a user
    List<GranterApplication> findByUser(User user);

    // 🔹 Find by status
    List<GranterApplication> findByStatus(String status);

    // 🔹 Find by user + status
    List<GranterApplication> findByUserAndStatus(User user, String status);
}