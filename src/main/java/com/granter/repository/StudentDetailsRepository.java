package com.granter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.granter.entity.StudentDetails;

public interface StudentDetailsRepository extends JpaRepository<StudentDetails, Integer> {
}