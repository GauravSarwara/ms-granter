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
    @Column(name = "city")
    private String city;

    @Column(name = "status")
    private String status;
    
    @Column(name = "step")
    private String step;
    
    @Column(name = "birth_date")
    private String dateOfBirth;

    @Column(name = "university")
    private String university;
    
    @Column(name = "cource")
    private String cource;
    
    @Column(name = "cource_type")
    private String courceType;
    
    @Column(name = "year_of_study")
    private String yearOfStudy;
    
    
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