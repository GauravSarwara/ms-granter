package com.granter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.granter.entity.GranterApplication;
import com.granter.entity.User;
@Repository
public interface GranterApplicationRepository extends JpaRepository<GranterApplication, Long> {

    // 🔹 Find all applications of a user
    List<GranterApplication> findByUser(User user);

    // 🔹 Find by status
    List<GranterApplication> findByStatus(Boolean status);

    // 🔹 Find by user + status
    List<GranterApplication> findByUserAndStatus(User user, Boolean status);
}