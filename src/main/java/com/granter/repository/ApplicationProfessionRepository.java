package com.granter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.granter.entity.ApplicationProfession;

public interface ApplicationProfessionRepository extends JpaRepository<ApplicationProfession, Integer> {
}