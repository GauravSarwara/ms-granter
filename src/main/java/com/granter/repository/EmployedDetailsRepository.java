package com.granter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.granter.entity.EmployedDetails;

public interface EmployedDetailsRepository extends JpaRepository<EmployedDetails, Integer> {
}