package com.granter.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profession")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;
}