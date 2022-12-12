package com.digitalbooks.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.digitalbooks.entities.Role;
import com.digitalbooks.entities.Roles;
import com.digitalbooks.entities.Subscription;
import com.digitalbooks.entities.User;
import com.digitalbooks.jwt.JwtUtils;
import com.digitalbooks.repositories.RoleRepository;
import com.digitalbooks.repositories.SubscriptionRepository;
import com.digitalbooks.repositories.UserRepository;
import com.digitalbooks.requests.Book;
import com.digitalbooks.requests.LoginRequest;
import com.digitalbooks.requests.SignupRequest;
import com.digitalbooks.requests.SubscriptionRequest;
import com.digitalbooks.responses.JwtResponse;
import com.digitalbooks.responses.MessageResponse;
import com.digitalbooks.userdetails.UserDetailsImpl;
import com.digitalbooks.userdetails.UserService;
import com.digitalbooks.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin(origins = "*" , maxAge = 3600)
//{"https://hoppscotch.io/","http://localhost:4200/"}
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
	
	@Value("${message:Hello world - Config Server is not working..Please check configuration }")
	private String message;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	ObjectMapper objectMapper;
	
	
	/*
	 * Guest can sign-up as reader or author to read or create books
	 */
	@PostMapping("/sign-up")
	public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (Boolean.TRUE.equals(userRepository.existsByUserName(signUpRequest.getUserName()))) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(UserUtils.USERNAME_ALREADY_TAKEN));
		}

		if (Boolean.TRUE.equals(userRepository.existsByEmailId(signUpRequest.getEmailId()))) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(UserUtils.EMAILID_ALREADY_TAKEN));
		}

		if (Boolean.TRUE.equals(userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber()))) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(UserUtils.PHONENUMBER_ALREADY_TAKEN));
		}
		
		User user = new User(signUpRequest.getUserName(), 
							 encoder.encode(signUpRequest.getPassword()),
									 signUpRequest.getEmailId(), signUpRequest.getPhoneNumber());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByRole(Roles.ROLE_READER)
					.orElseThrow(() -> new RuntimeException(UserUtils.ROLE_NOT_FOUND));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				if("author".equalsIgnoreCase(role)) {
					Role authorRole = roleRepository.findByRole(Roles.ROLE_AUTHOR)
							.orElseThrow(() -> new RuntimeException(UserUtils.ROLE_NOT_FOUND));
					roles.add(authorRole);
				} else {
					Role userRole = roleRepository.findByRole(Roles.ROLE_READER)
							.orElseThrow(() -> new RuntimeException(UserUtils.ROLE_NOT_FOUND));
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
	public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		Set<Subscription> subscriptions = userDetails.getSubscriptions().stream().filter(Subscription::isActive).collect(Collectors.toSet());

		return ResponseEntity.ok(new JwtResponse(jwt, 
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles,
												 subscriptions));
	}
	
	/*
	 * Author can create a book
	 */
	@PostMapping("/author/{author-id}/books")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<MessageResponse> createABook(HttpServletRequest request, @RequestBody Book book, @PathVariable("author-id") Long id) {
		if (ObjectUtils.isEmpty(id))
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.AUTHORID_INVALID));
		
		String jwt = jwtUtils.parseJwt(request);
		if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
			String authorName = jwtUtils.getUserNameFromJwtToken(jwt);
			book.setAuthorName(authorName);
		} else {
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
		}
		
		return userService.createBook(book, id);
	}
	
	/*
	 * Reader can subscribe to a book
	 */
	@PostMapping("/{book-id}/subscribe")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<MessageResponse> subscribeABook(@RequestBody SubscriptionRequest subscriptionRequest,
			@PathVariable("book-id") Long bookId) {
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.BOOKID_INVALID));
		
		return userService.subscribeABook(subscriptionRequest, bookId);
	}
	
	/*
	 * Reader can fetch a subscribed book
	 */
	@GetMapping("/readers/{user-id}/books/{subscription-id}")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> fetchSubscribedBook(@PathVariable("user-id") Long userId, @PathVariable("subscription-id") Long subscriptionId) {
		if (ObjectUtils.isEmpty(userId) || !userRepository.existsById(userId))
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, UserUtils.USERID_INVALID);
			//return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.USERID_INVALID));
		if (ObjectUtils.isEmpty(subscriptionId) || !subscriptionRepository.existsById(subscriptionId))
			return ResponseEntity.badRequest().body(new MessageResponse("subscriptionId is not valid"));

		return userService.fetchSubscribedBook(userId,subscriptionId);
	}
	
	/*
	 * Reader can fetch all his subscribed books
	 */
	@GetMapping("/readers/{user-id}/books")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<?> fetchAllSubscribedBooks(@PathVariable("user-id") Long userId) {
		if (ObjectUtils.isEmpty(userId) || !userRepository.existsById(userId))
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.USERID_INVALID));
		
		Set<Subscription> subscriptionsList = userService.getSubscriptions(userId);
		
		if(!subscriptionsList.isEmpty()) {
			
			List<Long> bookIds = subscriptionsList.stream().filter(Subscription::isActive).map(Subscription::getBookId).collect(Collectors.toList());
			
			String uri = bookServiceHost + "/book/getSubscribedBooks";
			
			restTemplate = new RestTemplate();
			ResponseEntity<?> result = restTemplate.postForEntity(uri, bookIds, List.class);
			return ResponseEntity.ok(result.getBody());
		}
		
		return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
	}

	/*
	 * Author can block a book created by him
	 */
	@PostMapping("/author/{author-id}/books/{book-id}")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<MessageResponse> blockABook(@PathVariable("author-id") int authorId, @PathVariable("book-id") int bookId, @RequestParam("block") boolean block) {
		if (ObjectUtils.isEmpty(authorId))
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.AUTHORID_INVALID));
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.BOOKID_INVALID));
		
		String uri = bookServiceHost + "/author/" + authorId + "/blockBook/" + bookId +"?block=" + block;

		MessageResponse result = restTemplate.getForObject(uri, MessageResponse.class);
		return getResultResponseEntity(result);
	}

	private ResponseEntity<MessageResponse> getResultResponseEntity(MessageResponse result) {
		if(result == null || result.getMessage().equals("Book updation failed"))
			return ResponseEntity.badRequest().body(result);
		return ResponseEntity.ok(result);
	}

	/*
	 * Author can update a book created by him
	 */
	@PutMapping("/author/{author-id}/updateBook/{book-id}")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> updateABook(@RequestBody Book book, @PathVariable("author-id") Long authorId, @PathVariable("book-id") Long bookId) {
		if (ObjectUtils.isEmpty(authorId))
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.AUTHORID_INVALID));
		if (ObjectUtils.isEmpty(bookId))
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.BOOKID_INVALID));
		
		
		return getResultResponseEntity(userService.updateBook(authorId,bookId, book));
	}
	
	/*
	 * Author can fetch all books created by him
	 */
	@GetMapping("/authors/{author-id}/getAllBooks")
	@PreAuthorize("hasRole('AUTHOR')")
	public ResponseEntity<?> getAuthorBooks(@PathVariable("author-id") Long authorId) {
		if (ObjectUtils.isEmpty(authorId))
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.AUTHORID_INVALID));
		
		return userService.getAuthorBooks(authorId);
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
	@SuppressWarnings("unchecked")
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
		if(result.getBody() == null)
			return ResponseEntity.badRequest().body(new MessageResponse("Bad Request"));
		List<Book> books = (List<Book>) result.getBody();
		if(books == null || books.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse("Invalid request"));
		return ResponseEntity.ok(books);
		
	}

	/*
	 * get user details
	 */
	@GetMapping("/readers/{user-id}")
	public ResponseEntity<?> getUserDetails(@PathVariable("user-id") Long id, HttpServletRequest httpServletRequest){
		if (id == null)
			return ResponseEntity.badRequest().body(new MessageResponse("invalid request"));
		
		String jwt = jwtUtils.parseJwt(httpServletRequest);
		if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
			String username = jwtUtils.getUserNameFromJwtToken(jwt);
			return userService.getUserDetails(id, username);
		} else {
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
		}
		
	}
	
	@RequestMapping("/getMessage")
	String getMessage() {
		return this.message;
	}
}
