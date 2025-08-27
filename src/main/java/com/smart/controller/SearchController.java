package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.model.Contact;
import com.smart.model.User;
import com.smart.repository.ContactRepo;
import com.smart.repository.UserRepo;

@RestController
public class SearchController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ContactRepo contactRepo;

	@GetMapping("search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal p) {
		System.out.println("query" +query);

		User user = userRepo.findByEmail(p.getName());

		List<Contact> contacts = contactRepo.findByNameContainingAndUser(query, user);

		System.out.println("contact details"+contacts);
		return ResponseEntity.ok(contacts);
	}
}
