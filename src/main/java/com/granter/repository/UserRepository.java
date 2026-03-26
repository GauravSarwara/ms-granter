package com.granter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.granter.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

}