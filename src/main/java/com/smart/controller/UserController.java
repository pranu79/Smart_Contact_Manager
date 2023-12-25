package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.model.User;
import com.smart.handler.message;
import com.smart.model.Contact;
import com.smart.model.MyOrder;
import com.smart.repository.ContactRepo;
import com.smart.repository.MyOrderRepo;
import com.smart.repository.UserRepo;
import com.smart.service.UserService;
import com.razorpay.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ContactRepo contactRepo;

	@Autowired
	private UserService userService;
	
	@Autowired
	private MyOrderRepo myOrderRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@ModelAttribute
	public void customUser(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user", user);

		}

	}

	@GetMapping("index")
	public String index() {
		return "/user/index";
	}

	@GetMapping("profile")
	public String profile() {
		return "/user/profile";
	}

	@GetMapping("allContacts/{page}")
	public String allContacts(@PathVariable("page") Integer page, Principal p, Model m) {
		String email = p.getName();
		User user = userRepo.findByEmail(email);

		PageRequest pageable = PageRequest.of(page, 3);
		Page<Contact> contacts = contactRepo.findContactsByUser(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		return "/user/allContacts";
	}

	@GetMapping("addContact")
	public String addContact(Model m) {
		m.addAttribute("contact", new Contact());
		return "/user/addContact";
	}

	@GetMapping("{cId}/Contact")
	public String Contact(@PathVariable("cId") int cid, Principal p, Model m) {

		Optional<Contact> contactOptional = contactRepo.findById(cid);
		Contact contact = contactOptional.get();

		String email = p.getName();
		User user = userRepo.findByEmail(email);

		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("contact", contact);

		}

		return "/user/Contact";
	}

	@PostMapping("addingContact")
	public String addingContact(@ModelAttribute Contact contact, @RequestParam("profileImg") MultipartFile file,
			Principal p, HttpSession session) {

		try {
			String email = p.getName();
			User user = userRepo.findByEmail(email);

			if (file.isEmpty()) {
				System.out.println("File is Empty");
				contact.setImage("contact.png");

			} else {

				contact.setImage(file.getOriginalFilename());

				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is uploaded");
				// System.out.println(path);
			}
			user.getContact().add(contact);
			contact.setUser(user);

			userService.createUser(user);

			// System.out.println("contact added");
			session.setAttribute("message", new message("Your Contact is Added!! Add More..", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			session.setAttribute("message", new message("Something went wrong", "alert-danger"));
		}

		return "redirect:/user/addContact";
	}

	@GetMapping("delete/{cid}")
	public String addContact(@PathVariable("cid") int cid, Model m, HttpSession session, Principal p) {

		Optional<Contact> contactOptional = contactRepo.findById(cid);
		Contact contact = contactOptional.get();

		User user = userRepo.findByEmail(p.getName());
		user.getContact().remove(contact);
		userRepo.save(user);

		session.setAttribute("message", new message("Contact Deleted Successfully!!", "alert-success"));

		return "redirect:/user/allContacts/0";
	}

	@PostMapping("updateContact/{cid}")
	public String updateContact(Model m, @PathVariable("cid") int cid) {

		Contact contact = contactRepo.findById(cid).get();

		m.addAttribute("contact", contact);
		return "/user/updateContact";
	}

	@PostMapping("updatingContact")
	public String updatingContact(@ModelAttribute Contact contact, Model m,
			@RequestParam("profileImg") MultipartFile file, Principal p, HttpSession session) {

		try {
			Contact oldDetails = contactRepo.findById(contact.getCid()).get();

			if (!file.isEmpty()) {

				// delete old img
				File oldFile = new ClassPathResource("static/img").getFile();
				File files = new File(oldFile, oldDetails.getImage());
				files.delete();

				// update new img
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldDetails.getImage());
			}

			System.out.println(contact.getCid());

			User user = userRepo.findByEmail(p.getName());
			contact.setUser(user);
			contactRepo.save(contact);

			session.setAttribute("message", new message("Your Contact is Updated", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/" + contact.getCid() + "/Contact";
	}

	@GetMapping("search")
	public String search() {
		
		return "/user/search";
	}

	@PostMapping("Searching")
	public String search(@RequestParam("query") String query, Principal p, Model m
			) {

		System.out.println(query);

		User user = userRepo.findByEmail(p.getName());
		

		List<Contact> contacts = contactRepo.findByNameContainingAndUser(query,user);

		System.out.println(contacts);
		m.addAttribute("contact", contacts);

		return "/user/search";

	}
	//payment integration
	//create order on server
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data,Principal p) throws Exception {
		System.out.println("hey order function ex.");
		System.out.println(data);
		int amount =Integer.parseInt(data.get("amount").toString());
		
		RazorpayClient client = new RazorpayClient("rzp_test_CJghtqd2CwnqLe", "h4Af6HZR9V3lOfZfZZa5hajj");
		
		JSONObject ob = new JSONObject();
		ob.put("amount", amount*100);
		ob.put("currency", "INR");
		ob.put("receipt", "tnx_123905");

		//creating new order
		Order order = client.orders.create(ob);
		System.out.println(order);
		
		//save the order in db
		MyOrder myorder = new MyOrder();
		myorder.setAmount(order.get("amount")+"");
		myorder.setOrderId(order.get("id"));
		myorder.setReceipt(order.get("receipt"));
		myorder.setStatus("created");
		myorder.setPaymentId(null);
		myorder.setUser(userRepo.findByEmail(p.getName()));
		
		myOrderRepo.save(myorder);
		//if you want to save data in db you can do it
		
		return order.toString();
	}
	
	@PostMapping("/update_order")
	public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {
		
		System.out.println(data);
		
		MyOrder myorder = myOrderRepo.findByOrderId(data.get("order_id").toString());
		myorder.setStatus(data.get("status").toString());
		myorder.setPaymentId(data.get("payment_id").toString());
		myOrderRepo.save(myorder);
		return ResponseEntity.ok(Map.of("msg","Updated"));
	}
	
	@GetMapping("setting")
	public String setting() {
		return "/user/settings";
	}
	
	@PostMapping("changingPassword")
	public String updatePassword(@RequestParam("password") String password, @RequestParam("cpassword") String cpassword,Principal p,HttpSession session) {
		
		System.out.println(password);
		System.out.println(cpassword);
		
		String email = p.getName();
		User user =userRepo.findByEmail(email);
		
		//String oldpassword = passwordEncoder.encode(password);
		
		if(passwordEncoder.matches(password, user.getPassword())) {
			user.setPassword(passwordEncoder.encode(cpassword));
			userRepo.save(user);
			session.setAttribute("message", new message("Password changed Successfully..", "alert-success"));
			return "redirect:/user/index";
		}
		
		session.setAttribute("message", new message("Password is incorrect", "alert-danger"));

		return "redirect:/user/setting";
	}

}
