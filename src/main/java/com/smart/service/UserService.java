package com.smart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.smart.model.User;
import com.smart.repository.UserRepo;

import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {
	
	@Autowired
	private UserRepo userRepo;
	
	public User createUser(User user) {
		return userRepo.save(user);
	}
	
	public void removeMsg() {
		HttpSession session = ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
		session.removeAttribute("message");
	}
	

}
