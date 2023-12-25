package com.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.model.User;

public interface UserRepo extends JpaRepository<User, Integer>{
	
	public User findByEmail(String email);

}
