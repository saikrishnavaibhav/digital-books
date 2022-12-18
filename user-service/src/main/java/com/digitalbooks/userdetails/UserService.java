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
import org.springframework.http.HttpStatus;
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
import com.digitalbooks.responses.SubscriptionResponse;
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
		Set<Subscription> subscriptions = getSubscriptions(userId);
		if(subscriptions == null) return null;
		Subscription subscription = subscriptions.stream().filter(sub -> sub.getId().equals(subscriptionId) && sub.isActive())
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
		logger.debug("cancel Subscription request for user: {} with subscriptionId: {}", userId, subscriptionId);
		Subscription subscription = getSubscription(userId, subscriptionId);
		if( subscription != null) {
			int twentyFourHours = 24 * 60 * 60 * 1000;
			if (System.currentTimeMillis() - subscription.getSubscriptionTime().getTime() > twentyFourHours )
				return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
			 
			logger.debug("cancelling Subscription:");
			subscription.setActive(false);
			subscriptionRepository.save(subscription);
			return ResponseEntity.ok().build();
		}
		logger.debug("Invalid Subscription");
		return ResponseEntity.badRequest().body(new MessageResponse("Invalid Subscription"));
	}

	public ResponseEntity<?> subscribeABook(SubscriptionRequest subscriptionRequest, Long bookId) {
		logger.debug(subscriptionRequest.toString());
		logger.debug("bookId: {}",bookId);
		Subscription subscription = new Subscription();
		subscription.setActive(subscriptionRequest.isActive());
		subscription.setBookId(bookId);
		subscription.setSubscriptionTime(subscriptionRequest.getSubscriptionTime());
		subscription.setUserId(subscriptionRequest.getUserId());
		
		Optional<User> isUserPresent = userRepository.findById(subscription.getUserId());
		if (isUserPresent.isPresent()) {
			List<Subscription> subscriptionsList = subscriptionRepository.findByBookIdAndUserId(bookId, subscriptionRequest.getUserId());
			Set<Subscription> activeSubscriptions = subscriptionsList.stream().filter(Subscription::isActive).collect(Collectors.toSet());
			if(activeSubscriptions.isEmpty()) {
				String uri = bookServiceHost + "/book/" + bookId + "/checkBook";
				
				String value= restTemplate.getForObject(uri, String.class);
				if(Boolean.TRUE.equals("BookFound".equalsIgnoreCase(value))) {
					
					User user = isUserPresent.get();
					Set<Subscription> subscriptions = user.getSubscriptions();
					subscriptions.add(subscription);
					user = userRepository.save(user);
					Subscription savedSubscription= user.getSubscriptions().stream().filter(sub -> ((subscription.getBookId() == sub.getBookId()) && sub.isActive()))
					.findFirst().orElse(null);
					if(savedSubscription != null) {
						logger.debug("Subscription suucessful");
						logger.debug(savedSubscription.toString());
						return ResponseEntity.ok(new SubscriptionResponse(savedSubscription.getId(), savedSubscription.getSubscriptionTime()));
					}
				}
			}
		}
		logger.debug(UserUtils.INVALID_REQUEST);
		return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));

	}

	public ResponseEntity<?> createBook( Book book, Long id) {
		logger.debug("calling book service to create book");
		String uri = bookServiceHost + author + id + "/createBook";
		ResponseEntity<?> result = restTemplate.postForObject(uri,book, ResponseEntity.class);
		return result;
	}

	public ResponseEntity<?> fetchSubscribedBook(Long userId, Long subscriptionId) {
		Subscription subscription = getSubscription(userId, subscriptionId);
		if(subscription != null && !ObjectUtils.isEmpty(subscription.getBookId())) {
			String uri = bookServiceHost + "/book/" + subscription.getBookId() + "/getSubscribedBook";
			logger.debug("calling book service to get subscried book: {}",subscription.getBookId());
			ResponseEntity<?> result = restTemplate.getForEntity(uri, BookResponse.class);
			logger.debug("result: {}", result);
			if(result.getStatusCode().equals(HttpStatus.OK))
				return ResponseEntity.ok(result.getBody()); 
			return ResponseEntity.badRequest().body(result.getBody());
		}
		
		return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
	}

	public ResponseEntity<?> getAuthorBooks(Long authorId) {
		logger.debug("calling book service to get author books: {}",authorId);
		String uri = bookServiceHost + author + authorId + "/getAuthorBooks";

		return restTemplate.getForEntity(uri, List.class);
	}

	public ResponseEntity<?> updateBook(Long authorId, Long bookId, Book book) {
		logger.debug("calling book service to update book: {}",bookId);
		String uri = bookServiceHost + author + authorId + "/updateBook/" + bookId;
		restTemplate.put(uri,book);
		return ResponseEntity.ok().build();
	}

//	public ResponseEntity<?> getUserDetails(Long id, String username) {
//		Optional<User> isUserPresent = userRepository.findById(id);
//		if(isUserPresent.isPresent() && username.equals(isUserPresent.get().getUserName())) {
//			User user = isUserPresent.get();
//			user.setSubscriptions(user.getSubscriptions().stream().filter(Subscription::isActive).collect(Collectors.toSet()));
//			return ResponseEntity.ok(isUserPresent.get());
//		}
//		return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
//	}


	public ResponseEntity<?> blockBook(Long bookId, Long authorId, boolean block) {
		String uri = bookServiceHost + "/author/" + authorId + "/blockBook/" + bookId +"?block=" + block;
		ResponseEntity<?> result = restTemplate.getForObject(uri, ResponseEntity.class);
		return result;
	}

	public ResponseEntity<?> fetchAllSubscribedBooks(Long userId) {
		
		Set<Subscription> subscriptionsList = getSubscriptions(userId);
		
		if(subscriptionsList != null && !subscriptionsList.isEmpty()) {
			logger.debug("calling book service to get subscribed books of: {}",userId);
			List<Long> bookIds = subscriptionsList.stream().filter(Subscription::isActive).map(Subscription::getBookId).collect(Collectors.toList());
			
			String uri = bookServiceHost + "/book/getSubscribedBooks";
			ResponseEntity<?> result = restTemplate.postForEntity(uri, bookIds, List.class);
			
			return ResponseEntity.ok(result.getBody());
		}
		
		return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
	}


	@SuppressWarnings("unchecked")
	public ResponseEntity<?> searchBooks(String category, String title, String author) {
		String uri = bookServiceHost + "/book/searchBooks?category="+category+"&title="+title+"&author="+author;
		logger.info("category: {}, title: {}, author: {}",category,title,author);
		ResponseEntity<?> result = restTemplate.getForEntity(uri, List.class);
		if(result.getBody() == null)
			return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
		List<Book> books = (List<Book>) result.getBody();
		if(books == null || books.isEmpty()) return ResponseEntity.badRequest().body(new MessageResponse(UserUtils.INVALID_REQUEST));
		return ResponseEntity.ok(books);
	}
	
}
