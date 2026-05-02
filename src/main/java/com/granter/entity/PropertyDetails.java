package com.granter.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "property_details")
public class PropertyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "accommodation_type")
    private String accommodationType;

    @Column(name = "landlord_name")
    private String landlordName;

    @Column(name = "property_address")
    private String propertyAddress;

    @Column(name = "monthly_rent")
    private String monthlyRent;

    @Column(name = "tenancy_start_date")
    private String tenancyStartDate;

    @Column(name = "tenancy_end_date")
    private String tenancyEndDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 🔗 Many properties can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granter_id", nullable = false)
    private GranterApplication granterApplication ;

    // ===== Lifecycle Hooks =====
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== Getters & Setters =====
}