package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.handler.message;
import com.smart.model.User;
import com.smart.repository.UserRepo;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class forgotPasswordController {
	
	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@GetMapping("/forgotPassword")
	public String forgot_password() {
		return "forgotPassword";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session) {
		
		System.out.println(email);
		
		//generating 4 digit otp
		
		Random random = new Random(1000);
		int otp = random.nextInt(999999);
		System.out.println("OTP" + otp);
		
		//writing code for sending otp to email
		String subject = "OTP from SCM";
		String message = "<p>Your OTP for email verification is <b>"+otp+"</b></p>";
		String to = email;
		
		boolean flag = emailService.sendEmail(to, subject, message);
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else {
			session.setAttribute("message", new message("Check your email ID", "alert-danger"));

			return "forgotPassword";
		}
		
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp,HttpSession session) {
		
		int myotp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");
		
		if(myotp==otp) {
			User user =userRepo.findByEmail(email);
			
			if(user==null) {
				session.setAttribute("message", new message("user does not exist!!", "alert-danger"));
				return "forgotPassword";
			}
			else {
				return "change_password_form";
			}
			
		}
		else {
			session.setAttribute("message", new message("OTP is incorrect", "alert-danger"));
			return "verify_otp";
		}
		
	}
	
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		String email = (String) session.getAttribute("email");
		User user =userRepo.findByEmail(email);
		user.setPassword(passwordEncoder.encode(newpassword));
		userRepo.save(user);
		
		return "redirect:/login?change=Password changed successfully..";
	}
	
	

}
