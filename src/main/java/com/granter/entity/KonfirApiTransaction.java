package com.granter.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "konfir_api_transaction")
@Data
public class KonfirApiTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "candidate_id")
    private String candidateId;

    @Column(name = "employment_activity_id")
    private String employmentActivityId;

    @Column(name = "education_activity_id")
    private String educationActivityId;

    @Column(name = "self_employment_activity_id")
    private String selfEmploymentActivityId;

    @Column(name = "request_json")
    private String requestJson;

    @Column(name = "response_json")
    private String responseJson;

    @Column(name = "status")
    private String status;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
}