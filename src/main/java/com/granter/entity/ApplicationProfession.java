package com.granter.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "application_profession")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationProfession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private GranterApplication application;

    @ManyToOne
    @JoinColumn(name = "profession_id", nullable = false)
    private Profession profession;
}