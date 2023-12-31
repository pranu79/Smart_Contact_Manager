package com.smart.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.model.Contact;
import com.smart.model.User;

public interface ContactRepo extends JpaRepository<Contact, Integer>{
	
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int Userid,Pageable pageable);
	
	//search
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
	
	

}
