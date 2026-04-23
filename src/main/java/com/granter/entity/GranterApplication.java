package com.granter.entity;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "granter_application")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GranterApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    private String birthDate;

    private Boolean status;

    private String step;

    private OffsetDateTime createdAt;
    private String createdBy;

    private OffsetDateTime updatedAt;
    private String updatedBy;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 🔗 Mapping to professions
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationProfession> professions;

    // 🔗 One-to-one mappings
    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private StudentDetails studentDetails;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private EmployedDetails employedDetails;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private SelfEmployedDetails selfEmployedDetails;
}