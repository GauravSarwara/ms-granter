package com.granter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.granter.entity.SelfEmployedDetails;

public interface SelfEmployedDetailsRepository extends JpaRepository<SelfEmployedDetails, Integer> {
}