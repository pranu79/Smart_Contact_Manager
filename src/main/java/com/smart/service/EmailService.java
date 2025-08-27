package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;


@Service
public class EmailService {
	
	public boolean sendEmail(String to ,String subject,String message) {
		
		boolean f = false;
		
		String from = "gh2023raisoni@gmail.com";
		
		//variable for gmail
		String host = "smtp.gmail.com";
		
		//get system properties
		
		Properties properties = System.getProperties();
		System.out.println("properties" + properties);
		
		//setting imp info to properties object
		
		//host set
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		String username = "gh2023raisoni";
		String password= "kqrncswveawykpfs";
		
		//step1 to get session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(username,password);
			}
			
			
		});
		session.setDebug(true);
		
		//step2 compose the message [text,multi media]
		
		MimeMessage m = new MimeMessage(session);
		
		try {
			//from email
			
			m.setFrom(from);
			
			//adding recipient  to message
			
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//adding subject to message
			
			m.setSubject(subject);
			
			//adding text message
			
			//m.setText(message);
			m.setContent(message,"text/html");
			
			
			//send
			
			//step3 send message using transport class
			
			Transport.send(m);
			
			System.out.println("message send successfully....");
			f = true;
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		return f;
		
	}

}
