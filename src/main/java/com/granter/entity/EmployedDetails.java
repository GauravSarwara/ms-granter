package com.granter.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employed_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployedDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String employerName;
    private String employerEmail;
    private String monthlySalary;
    private String dateOfJoining;
    private String contractType;
    
    @Column(name = "employee_id", nullable = false, unique = true, updatable = false)
    private UUID employeeId;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private GranterApplication application;
}