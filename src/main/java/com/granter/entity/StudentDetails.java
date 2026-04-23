package com.granter.entity;

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
@Table(name = "student_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String university;
    private String course;
    private String courseStartDate;
    private String courseEndDate;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private GranterApplication application;
}