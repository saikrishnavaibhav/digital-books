package com.digitalbooks.userdetails;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
		
		Set<Subscription> subscriptionsList = new HashSet<>();
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
			int HOURS_24 = 24 * 60 * 60 * 1000;
			System.out.println(System.currentTimeMillis());
			System.out.println(subscription.getSubscriptionTime().getTime());
			System.out.println(System.currentTimeMillis() - subscription.getSubscriptionTime().getTime() > HOURS_24 );
			if (System.currentTimeMillis() - subscription.getSubscriptionTime().getTime() > HOURS_24 )
				return ResponseEntity.badRequest().body(new MessageResponse("Invalid request"));
			 
			
			subscription.setActive(false);
			subscriptionRepository.save(subscription);
			return ResponseEntity.ok(new MessageResponse("Cancelled to subscription successfully!"));
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
			
			String uri = bookServiceHost + "/book/" + bookId + "/checkBook";
			
			Boolean bookExist = false;
			String value= restTemplate.getForObject(uri, String.class);
			bookExist = "BookFound".equalsIgnoreCase(value) ? true : false;
			if(bookExist) {
				
				User user = isUserPresent.get();
				Set<Subscription> subscriptions = user.getSubscriptions();
				subscriptions.add(subscription);
				userRepository.save(user);
				return ResponseEntity.ok(new MessageResponse("subscribed successfully"));
			} else 
				return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.BOOKID_INVALID));
		
		} else 
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.USERID_INVALID));
		
	}

	public ResponseEntity<MessageResponse> createBook( Book book, Long id) {
		
		String uri = bookServiceHost + "/author/" + id + "/createBook";

		MessageResponse result = restTemplate.postForObject(uri,book, MessageResponse.class);
		return ResponseEntity.ok(result);
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
	
}
