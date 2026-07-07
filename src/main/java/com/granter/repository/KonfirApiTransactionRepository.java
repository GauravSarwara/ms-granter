package com.granter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.granter.entity.KonfirApiTransaction;

@Repository
public interface KonfirApiTransactionRepository extends JpaRepository<KonfirApiTransaction, Long> {

    Optional<KonfirApiTransaction> findByCandidateId(String candidateId);

    Optional<KonfirApiTransaction> findByEmploymentActivityId(String employmentActivityId);

    Optional<KonfirApiTransaction> findByEducationActivityId(String educationActivityId);

    Optional<KonfirApiTransaction> findBySelfEmploymentActivityId(String selfEmploymentActivityId);
    
    Optional<KonfirApiTransaction> findByUserId(Long userId);
}