package com.digitalbooks.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.digitalbooks.entities.Book;
import com.digitalbooks.entities.Role;
import com.digitalbooks.entities.Roles;
import com.digitalbooks.entities.User;
import com.digitalbooks.jwt.JwtUtils;
import com.digitalbooks.repositories.RoleRepository;
import com.digitalbooks.repositories.UserRepository;
import com.digitalbooks.requests.LoginRequest;
import com.digitalbooks.requests.SignupRequest;
import com.digitalbooks.responses.ErrorResponse;
import com.digitalbooks.responses.JwtResponse;
import com.digitalbooks.responses.MessageResponse;
import com.digitalbooks.userdetails.UserDetailsImpl;

@RestController
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/v1/digitalbooks")
@RefreshScope
public class UserController {
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@PostMapping("/sign-up")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUserName(signUpRequest.getUserName())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: UserName is already taken!"));
		}

		if (userRepository.existsByEmailId(signUpRequest.getEmailId())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: EmailId is already in use!"));
		}

		if (userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: PhoneNumber is already in use!"));
		}
		
		User user = new User(signUpRequest.getUserName(), 
							 encoder.encode(signUpRequest.getPassword()),
									 signUpRequest.getEmailId(), signUpRequest.getPhoneNumber());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByRole(Roles.ROLE_READER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "author":
					Role authorRole = roleRepository.findByRole(Roles.ROLE_AUTHOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(authorRole);

					break;
				default:
					Role userRole = roleRepository.findByRole(Roles.ROLE_READER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PostMapping("/sign-in")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}
	
	@PostMapping("/author/{author-id}/books")
	public ResponseEntity<?> createABook(@Valid @RequestBody Book book, @PathVariable("author-id") int id) {
		if (ObjectUtils.isEmpty(id))
			return ResponseEntity.badRequest().body(new MessageResponse("Author id is not valid"));
		String uri = "http://localhost:8082/api/v1/digitalbooks/author/" + id + "/createBook";

		RestTemplate restTemplate = new RestTemplate();
		try {
			MessageResponse result = restTemplate.postForObject(uri,book, MessageResponse.class);
			return ResponseEntity.ok(result);
		} catch (Exception exception) {
			return ResponseEntity.internalServerError()
					.body(new ErrorResponse(exception.getMessage(), exception.getCause()));
		}

	}

}
