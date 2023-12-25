package com.smart.controller;

import java.security.Principal;

import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.handler.message;
import com.smart.model.User;
import com.smart.repository.UserRepo;
import com.smart.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepo userRepo;

	@ModelAttribute
	public void commonUser(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user", user);
		}

	}

	@GetMapping("/index")
	public String index() {
		return "index";
	}

	@GetMapping("/signup")
	public String register(Model m) {
		m.addAttribute("user", new User());
		return "register";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@PostMapping("/createUser")
	public String createUser( @ModelAttribute("user") User user,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,HttpSession session) {
		try {

			if (!agreement) {
				System.out.println("Please Agree terms and Conditions");
				throw new Exception("Please Agree terms and Conditions");
			}
			
			/*
			 * if(res.hasErrors()) { System.err.println("ERROR" + res.toString());
			 * m.addAttribute("user",user); return "redirect:/signup"; }
			 */

			user.setEnabled(true);
			user.setRole("ROLE_USER");
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println(agreement);
			System.out.println(user);

			User result = userService.createUser(user);

			m.addAttribute("user", new User());
			session.setAttribute("message", new message("Registered Successfully... ", "alert-success"));

			return "redirect:/signup";

		} catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user", user);
			session.setAttribute("message", new message(e.getMessage(), "alert-danger"));
			return "redirect:/signup";
		}

	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}
	
	@GetMapping("/invalid")
	public String invalid() {
		return "invalid";
	}
	
	

}
