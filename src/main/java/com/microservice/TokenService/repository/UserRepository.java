package com.microservice.TokenService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.TokenService.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Integer>{

   Optional<UserEntity> findByUsername(String username);

   boolean existsByUsername(String username);
	
}
