package com.digitalbooks.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.digitalbooks.entities.Book;
import com.digitalbooks.entities.Role;
import com.digitalbooks.entities.Roles;
import com.digitalbooks.entities.Subscription;
import com.digitalbooks.entities.User;
import com.digitalbooks.jwt.JwtUtils;
import com.digitalbooks.repositories.RoleRepository;
import com.digitalbooks.repositories.SubscriptionRepository;
import com.digitalbooks.repositories.UserRepository;
import com.digitalbooks.requests.LoginRequest;
import com.digitalbooks.requests.SignupRequest;
import com.digitalbooks.responses.JwtResponse;
import com.digitalbooks.responses.MessageResponse;
import com.digitalbooks.userdetails.UserDetailsImpl;
import com.digitalbooks.userdetails.UserService;

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
	SubscriptionRepository subscriptionRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	UserService userService;
	
	@Value("${bookservice.host}")
	private String bookServiceHost;
	
	RestTemplate restTemplate;
	
	/*
	 * Guest can sign-up as reader or author to read or create books
	 */
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
		user.setSubscriptions(signUpRequest.getSubscriptions());
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	/*
	 * Guest can sign-in using valid credentials
	 */
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
												 roles,
												 userDetails.getSubscriptions()));
	}
	
	/*
	 * Author can create a book
	 */
	@PostMapping("/author/{author-id}/books")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> createABook(@Valid @RequestBody Book book, @PathVariable("author-id") Long id) {
		if (ObjectUtils.isEmpty(id))
			return ResponseEntity.badRequest().body(new MessageResponse("Author id is not valid"));
		String uri = bookServiceHost + "/author/" + id + "/createBook";

		RestTemplate restTemplate = new RestTemplate();

		MessageResponse result = restTemplate.postForObject(uri,book, MessageResponse.class);
		return ResponseEntity.ok(result);
	}
	
	/*
	 * Reader can subscribe to a book
	 */
	@PostMapping("/{book-id}/subscribe")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> subscribeABook(@RequestBody Subscription subscription,
			@PathVariable("book-id") int bookId) {
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse("bookId is not valid"));

		Optional<User> isUserPresent = userRepository.findById(subscription.getUserId());
		if (isUserPresent.isPresent()) {
			User user = isUserPresent.get();
			Set<Subscription> subscriptions = user.getSubscriptions();
			subscriptions.add(subscription);
			return ResponseEntity.ok(userRepository.save(user));
		}
		return ResponseEntity.badRequest().body(new MessageResponse("user not found"));
	}
	
	/*
	 * Reader can fetch a subscribed book
	 */
	@GetMapping("/readers/{user-id}/books/{subscription-id}")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> fetchSubscribedBook(@PathVariable("user-id") Long userId, @PathVariable("subscription-id") Long subscriptionId) {
		if (ObjectUtils.isEmpty(userId) || !userRepository.existsById(userId))
			return ResponseEntity.badRequest().body(new MessageResponse("userId is not valid"));
		if (ObjectUtils.isEmpty(subscriptionId) || !subscriptionRepository.existsById(subscriptionId))
			return ResponseEntity.badRequest().body(new MessageResponse("subscriptionId is not valid"));

		Subscription subscription = userService.getSubscription(userId, subscriptionId);
		if(subscription != null && !ObjectUtils.isEmpty(subscription.getBookId())) {
			String uri = bookServiceHost + "/book/" + subscription.getBookId() + "/getSubscribedBook";
			
			restTemplate = new RestTemplate();
			ResponseEntity<?> result = restTemplate.getForEntity(uri, Book.class);
			return ResponseEntity.ok(result.getBody());
		}
		
		return ResponseEntity.badRequest().body(new MessageResponse("invalid request"));
	}
	
	/*
	 * Reader can fetch all his subscribed books
	 */
	@GetMapping("/readers/{user-id}/books")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> fetchSubscribedBook(@PathVariable("user-id") Long userId) {
		if (ObjectUtils.isEmpty(userId) || !userRepository.existsById(userId))
			return ResponseEntity.badRequest().body(new MessageResponse("userId is not valid"));
		
		Set<Subscription> subscriptionsList = userService.getSubscriptions(userId);
		System.out.println(subscriptionsList);
		if(!subscriptionsList.isEmpty()) {
			
			List<Long> bookIds = subscriptionsList.stream().map(sub -> sub.getBookId()).collect(Collectors.toList());
			
			String uri = bookServiceHost + "/book/getSubscribedBooks";
			
			restTemplate = new RestTemplate();
			ResponseEntity<?> result = restTemplate.postForEntity(uri, bookIds, List.class);
			return ResponseEntity.ok(result.getBody());
		}
		
		return ResponseEntity.badRequest().body(new MessageResponse("invalid request"));
	}

	/*
	 * Author can block a book created by him
	 */
	@PostMapping("/author/{author-id}/books/{book-id}")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> blockABook(@PathVariable("author-id") int authorId, @PathVariable("book-id") int bookId, @RequestParam("block") boolean block) {
		if (ObjectUtils.isEmpty(authorId))
			return ResponseEntity.badRequest().body(new MessageResponse("Author id is not valid"));
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse("Book id is not valid"));
		
		String uri = bookServiceHost + "/author/" + authorId + "/blockBook/" + bookId +"?block=" + block;

		RestTemplate restTemplate = new RestTemplate();

		MessageResponse result = restTemplate.getForObject(uri, MessageResponse.class);
		return getResultResponseEntity(result);
	}

	private ResponseEntity<?> getResultResponseEntity(MessageResponse result) {
		if(result.getMessage().equals("Book updation failed"))
			return ResponseEntity.badRequest().body(result);
		return ResponseEntity.ok(result);
	}

	/*
	 * Author can update a book created by him
	 */
	@PutMapping("/author/{author-id}/books/{book-id}")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> updateABook(@RequestBody Book book, @PathVariable("author-id") Long authorId, @PathVariable("book-id") Long bookId) {
		if (ObjectUtils.isEmpty(authorId))
			return ResponseEntity.badRequest().body(new MessageResponse("Author id is not valid"));
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse("Book id is not valid"));
		
		String uri = bookServiceHost + "/author/" + authorId + "/updateBook/" + bookId;

		RestTemplate restTemplate = new RestTemplate();

		MessageResponse result = restTemplate.postForObject(uri,book, MessageResponse.class);
		return getResultResponseEntity(result);
	}

	/*
	 * Reader can cancel subscription before 24 hrs
	 */
	@PostMapping("/readers/{user-id}/books/{subscription-id}/cancel-subscription")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> cancelSubscription(@PathVariable("user-id") Long userId, @PathVariable("subscription-id") Long subscriptionId) {
		if (ObjectUtils.isEmpty(userId))
			return ResponseEntity.badRequest().body(new MessageResponse("user id is not valid"));
		if (ObjectUtils.isEmpty(subscriptionId))
			return ResponseEntity.badRequest().body(new MessageResponse("subscription id is not valid"));
		
		
		return userService.cancelSubscription(userId, subscriptionId);
	}

	/*
	 * Reader can a read a subscribed book
	 */
	@GetMapping("/readers/{user-id}/books/{subscription-id}/read")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> readBook(@PathVariable("user-id") Long userId, @PathVariable("subscription-id") Long subscriptionId) {
		if (ObjectUtils.isEmpty(userId))
			return ResponseEntity.badRequest().body(new MessageResponse("user id is not valid"));
		if (ObjectUtils.isEmpty(subscriptionId))
			return ResponseEntity.badRequest().body(new MessageResponse("subscription id is not valid"));
		Subscription subscription = userService.getSubscription(userId, subscriptionId);
		if(subscription != null && !ObjectUtils.isEmpty(subscription.getBookId()))
		{
			String uri = bookServiceHost + "/book/" + subscription.getBookId() + "/readBook";
			
			restTemplate = new RestTemplate();
			MessageResponse result = restTemplate.getForObject(uri, MessageResponse.class);
			return ResponseEntity.ok(result);
		}
		return ResponseEntity.badRequest().body(new MessageResponse("Invalid request"));
		
	}
	
	/*
	 * Anyone can search books
	 */
	@GetMapping("/search")
	public ResponseEntity<?> searchBooks(@RequestParam("category") String category, @RequestParam("title") String title,
				@RequestParam("author") String author, @RequestParam("price") int price,  @RequestParam("publisher") String publisher) {
		if (ObjectUtils.isEmpty(category))
			return ResponseEntity.badRequest().body(new MessageResponse("category is not valid"));
		if (ObjectUtils.isEmpty(title))
			return ResponseEntity.badRequest().body(new MessageResponse("title is not valid"));
		if (ObjectUtils.isEmpty(author))
			return ResponseEntity.badRequest().body(new MessageResponse("author is not valid"));
		if (ObjectUtils.isEmpty(publisher))
			return ResponseEntity.badRequest().body(new MessageResponse("publisher is not valid"));
		if (price < 0)
			return ResponseEntity.badRequest().body(new MessageResponse("price is not valid"));
		
		String uri = bookServiceHost + "/book/searchBooks?category="+category+"&title="+title+"&author="+author+"&price="+price+"&publisher="+publisher;
			
		restTemplate = new RestTemplate();
		ResponseEntity<?> result = restTemplate.getForEntity(uri, List.class);
		return ResponseEntity.ok(result);
		
	}
}
