package com.digitalbooks.userdetails;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import com.digitalbooks.entities.Subscription;
import com.digitalbooks.entities.User;
import com.digitalbooks.jwt.JwtUtils;
import com.digitalbooks.repositories.SubscriptionRepository;
import com.digitalbooks.repositories.UserRepository;
import com.digitalbooks.requests.Book;
import com.digitalbooks.requests.SubscriptionRequest;
import com.digitalbooks.responses.BookResponse;
import com.digitalbooks.responses.MessageResponse;
import com.digitalbooks.utils.UserUtils;

@Component
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SubscriptionRepository subscriptionRepository;
	
	@Value("${bookservice.host}")
	private String bookServiceHost;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	JwtUtils jwtUtils;
	
	String author = "/author/";
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Cacheable(value = "subscription")
	public Subscription getSubscription(Long userId, Long subscriptionId) {

		Subscription subscription = getSubscriptions(userId).stream().filter(sub -> sub.getId().equals(subscriptionId) && sub.isActive())
				.findAny().orElse(null);
		if (subscription != null) {
			return subscription;
		}

		return null;
	}
	
	public Set<Subscription> getSubscriptions(Long userId) {
		
		Set<Subscription> subscriptionsList = null;
		User user = verifyAndGetUser(userId);
		if(user != null)
			subscriptionsList = user.getSubscriptions();

		return subscriptionsList;
	}

	public User verifyAndGetUser(Long userId) {
		Optional<User> isUserAvailable = userRepository.findById(userId);

		return isUserAvailable.isPresent() ? isUserAvailable.get() : null;
	}

	public ResponseEntity<?> cancelSubscription(Long userId, Long subscriptionId) {
		Subscription subscription = getSubscription(userId, subscriptionId);
		if( subscription != null) {
			int millis = 24 * 60 * 60 * 1000;
			if (System.currentTimeMillis() - subscription.getSubscriptionTime().getTime() > millis )
				return ResponseEntity.badRequest().body(new MessageResponse("Invalid request"));
			 
			
			subscription.setActive(false);
			subscriptionRepository.save(subscription);
			return ResponseEntity.ok(new MessageResponse("Cancelled subscription successfully!"));
		}
		return ResponseEntity.badRequest().body(new MessageResponse("Invalid Subscription"));
	}

	public ResponseEntity<MessageResponse> subscribeABook(SubscriptionRequest subscriptionRequest, Long bookId) {
		
		Subscription subscription = new Subscription();
		subscription.setActive(subscriptionRequest.isActive());
		subscription.setBookId(bookId);
		subscription.setSubscriptionTime(subscriptionRequest.getSubscriptionTime());
		subscription.setUserId(subscriptionRequest.getUserId());
		
		Optional<User> isUserPresent = userRepository.findById(subscription.getUserId());
		if (isUserPresent.isPresent()) {
			List<Subscription> subscriptionsList =subscriptionRepository.findByBookIdAndUserId(bookId, subscriptionRequest.getUserId());
			Set<Subscription> activeSubscriptions = subscriptionsList.stream().filter(Subscription::isActive).collect(Collectors.toSet());
			if(activeSubscriptions.isEmpty()) {
				String uri = bookServiceHost + "/book/" + bookId + "/checkBook";
				
				String value= restTemplate.getForObject(uri, String.class);
				if(Boolean.TRUE.equals("BookFound".equalsIgnoreCase(value))) {
					
					User user = isUserPresent.get();
					Set<Subscription> subscriptions = user.getSubscriptions();
					subscriptions.add(subscription);
					userRepository.save(user);
					return ResponseEntity.ok(new MessageResponse("subscribed successfully"));
				} else 
					return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.BOOKID_INVALID));
			} else {
				return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
			}
		} else 
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.USERID_INVALID));
		
	}

	public ResponseEntity<?> createBook( Book book, Long id) {
		
		String uri = bookServiceHost + author + id + "/createBook";
		ResponseEntity<?> result = restTemplate.postForObject(uri,book, ResponseEntity.class);
		return result;
	}

	public ResponseEntity<?> fetchSubscribedBook(Long userId, Long subscriptionId) {
		Subscription subscription = getSubscription(userId, subscriptionId);
		if(subscription != null && !ObjectUtils.isEmpty(subscription.getBookId())) {
			String uri = bookServiceHost + "/book/" + subscription.getBookId() + "/getSubscribedBook";
			ResponseEntity<?> result = restTemplate.getForEntity(uri, BookResponse.class);
			return ResponseEntity.ok(result.getBody());
		}
		
		return ResponseEntity.badRequest().body(new MessageResponse("invalid request"));
	}

	public ResponseEntity<?> getAuthorBooks(Long authorId) {
		String uri = bookServiceHost + author + authorId + "/getAuthorBooks";

		return restTemplate.getForEntity(uri, List.class);
	}

	public ResponseEntity<?> updateBook(Long authorId, Long bookId, Book book) {
		String uri = bookServiceHost + author + authorId + "/updateBook/" + bookId;
		restTemplate.put(uri,book);
		return ResponseEntity.ok().build();
	}

	public ResponseEntity<?> getUserDetails(Long id, String username) {
		Optional<User> isUserPresent = userRepository.findById(id);
		if(isUserPresent.isPresent() && username.equals(isUserPresent.get().getUserName())) {
			User user = isUserPresent.get();
			user.setSubscriptions(user.getSubscriptions().stream().filter(Subscription::isActive).collect(Collectors.toSet()));
			return ResponseEntity.ok(isUserPresent.get());
		}
		return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
	}


	public ResponseEntity<?> blockBook(int bookId, int authorId, boolean block) {
		String uri = bookServiceHost + "/author/" + authorId + "/blockBook/" + bookId +"?block=" + block;
		ResponseEntity<?> result = restTemplate.getForObject(uri, ResponseEntity.class);
		return result;
	}

	public ResponseEntity<?> fetchAllSubscribedBooks(Long userId) {
		
		Set<Subscription> subscriptionsList = getSubscriptions(userId);
		
		if(subscriptionsList != null && !subscriptionsList.isEmpty()) {
			
			List<Long> bookIds = subscriptionsList.stream().filter(Subscription::isActive).map(Subscription::getBookId).collect(Collectors.toList());
			
			String uri = bookServiceHost + "/book/getSubscribedBooks";
			
			restTemplate = new RestTemplate();
			ResponseEntity<?> result = restTemplate.postForEntity(uri, bookIds, List.class);
			return ResponseEntity.ok(result.getBody());
		}
		
		return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
	}
	
}
