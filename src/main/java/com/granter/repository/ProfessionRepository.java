package com.granter.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.granter.entity.Profession;

public interface ProfessionRepository extends JpaRepository<Profession, Integer> {

    Profession findByName(String name);
    List<Profession> findByNameIn(Set<String> names);
}