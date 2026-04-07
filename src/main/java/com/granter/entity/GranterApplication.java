package com.granter.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "granter_application")
public class GranterApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Many applications can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Business Fields
    @Column(name = "address")
    private String address;

    @Column(name = "status")
    private Boolean status;
    
    @Column(name = "step")
    private String step;
    
    @Column(name = "birth_date")
    private String dateOfBirth;   
   
    
    @Column(name = "employer_name")
    private String employerName;
    
    @Column(name = "monthly_income")
    private String monthlyIncome;
    
    @Column(name = "university")
    private String university;
    
    // Audit Columns
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    // 🔹 Auto सेट audit fields
    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}