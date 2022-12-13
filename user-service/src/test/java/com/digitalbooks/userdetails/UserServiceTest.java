package com.digitalbooks.userdetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
    private RestTemplate restTemplate;
	
	@Mock
	UserRepository userRepository;
	
	@Mock
	SubscriptionRepository subscriptionRepository;
	
    @InjectMocks
    private UserService userService = new UserService();
    
    @Mock
    JwtUtils jwtUtils;
    
	@Value("${bookservice.host}")
	private String bookServiceHost;
	
	void testGetSubscription() {
		fail("Not yet implemented");
	}

	
	void testGetSubscriptions() {
		fail("Not yet implemented");
	}

	
	void testVerifyAndGetUser() {
		fail("Not yet implemented");
	}

	@Test
	void testCancelSubscription() {
		Optional<User> user1 = Optional.ofNullable(new User());
		Set<Subscription> subs = new HashSet<>();
		Subscription sub = getSubscripton();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    Date date = new Date(); 
		sub.setSubscriptionTime(formatter.format(date));
		subs.add(sub);
		user1.get().setSubscriptions(subs);
		when(userRepository.findById(any())).thenReturn(user1);
		
		ResponseEntity<?> res = userService.cancelSubscription(sub.getUserId(),sub.getId());
		
		assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	void testCancelSubscriptionAfter24hours() {
				
		Optional<User> user1 = Optional.ofNullable(new User());
		Set<Subscription> subs = new HashSet<>();
		Subscription sub = getSubscripton();
		subs.add(sub);
		user1.get().setSubscriptions(subs);
		when(userRepository.findById(any())).thenReturn(user1);
		
		ResponseEntity<?> res = userService.cancelSubscription(sub.getUserId(),sub.getId());
		
		assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
	}
	
	@Test
	void testCancelSubscriptionError() {
		Optional<User> user = Optional.ofNullable(new User());
		when(userRepository.findById(any())).thenReturn(user);
		
		
		Subscription sub = getSubscripton();
		
		ResponseEntity<?> res = userService.cancelSubscription(sub.getUserId(),sub.getId());
		assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
	}

	@Test
	void testSubscribeABook() {
		Optional<User> user = Optional.ofNullable(new User());
		
		Set<Subscription> subs = new HashSet<>();
		Subscription sub = getSubscripton();
		subs.add(sub);
		user.get().setSubscriptions(subs);
		List<Subscription> subsList = new ArrayList<>();
		sub.setActive(false);
		subsList.add(sub);
		
		when(userRepository.findById(any())).thenReturn(user);
    	when( subscriptionRepository.findByBookIdAndUserId(4L,3L)).thenReturn(subsList);
    	String uri = bookServiceHost + "/book/4/checkBook";
        Mockito
          .when(restTemplate.getForObject(uri, String.class))
          .thenReturn("BookFound");
    	
        ResponseEntity<MessageResponse> res = userService.subscribeABook(getSubscriptonRequest(), 4L);
 
        assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	void testSubscribeABookForError() {
		Optional<User> user = Optional.ofNullable(new User());
		
		Set<Subscription> subs = new HashSet<>();
		Subscription sub = getSubscripton();
		subs.add(sub);
		user.get().setSubscriptions(subs);
		List<Subscription> subsList = new ArrayList<>();
		subsList.add(sub);
		
		when(userRepository.findById(any())).thenReturn(user);
    	when( subscriptionRepository.findByBookIdAndUserId(4L,3L)).thenReturn(subsList);
    	
        ResponseEntity<MessageResponse> res = userService.subscribeABook(getSubscriptonRequest(), 4L);
 
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
	}
	
	@Test
	void testSubscribeABookWithBookExistFalse() {
		Optional<User> user = Optional.ofNullable(new User());
		when(userRepository.findById(any())).thenReturn(user);

    	String uri = bookServiceHost + "/book/4/checkBook";
        Mockito
          .when(restTemplate.getForObject(uri, String.class))
          .thenReturn("Invalid BookId");
    	
        ResponseEntity<MessageResponse> res = userService.subscribeABook(getSubscriptonRequest(), 4L);
 
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
	}
	
	@Test
	void testSubscribeABookWithUserNull() {
        ResponseEntity<MessageResponse> res = userService.subscribeABook(getSubscriptonRequest(), 4L);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
	}

	@Test
	void testCreateBook() {
		String uri = bookServiceHost + "/author/1/createBook";

		 Mockito
        .when(restTemplate.postForObject(uri,new Book(), ResponseEntity.class)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
		 ResponseEntity<?> res = userService.createBook( new Book() , 1L);
		 assertEquals(HttpStatus.CREATED, res.getStatusCode());
		
	}

	@Test
	void testFetchSubscribedBook() {
		Subscription subscription = getSubscripton();
		when(userRepository.findById(anyLong())).thenReturn(getUser());
		String uri = bookServiceHost + "/book/" + subscription.getBookId() + "/getSubscribedBook";
		
		 Mockito
         .when(restTemplate.getForEntity(uri, BookResponse.class))
         .thenReturn(ResponseEntity.ok(new BookResponse()));
		 ResponseEntity<?> res = userService.fetchSubscribedBook(subscription.getUserId(), subscription.getId());
		 assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	void testFetchSubscribedBookForError() {
		Subscription subscription = getSubscripton();
		ResponseEntity<?> res = userService.fetchSubscribedBook(subscription.getUserId(), subscription.getId());
		 assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
	}
	
	@Test
	void testGetAuthorBooks() {
		String uri = bookServiceHost + "/author/1/getAuthorBooks";

		 Mockito
         .when(restTemplate.getForEntity(uri, List.class))
         .thenReturn(ResponseEntity.ok().build());
		 
		 ResponseEntity<?> res = userService.getAuthorBooks(1L);
		 assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	void testUpdateBook() {
		String uri = bookServiceHost + "/author/1/updateBook/1";
		
		doNothing().when(restTemplate).put(uri,new Book());
		ResponseEntity<?> res = userService.updateBook(1L, 1L, new Book());
		assertEquals(HttpStatus.OK, res.getStatusCode());
	}
	
	@Test
	void testBlockBook() {
		String uri = bookServiceHost + "/author/1/blockBook/1?block=false";
		Mockito
        .when(restTemplate.getForObject(uri, ResponseEntity.class))
        .thenReturn(ResponseEntity.ok().build());
		ResponseEntity<?> result = userService.blockBook(1L, 1L, false);
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	void TestFetchAllSubscribedBooks() {
		Optional<User> user = Optional.ofNullable(new User());
		
		Set<Subscription> subs = new HashSet<>();
		Subscription sub = getSubscripton();
		subs.add(sub);
		user.get().setSubscriptions(subs);
		
		when(userRepository.findById(any())).thenReturn(user);
		
		String uri = bookServiceHost + "/book/getSubscribedBooks";
		List<Long> bookIds = new ArrayList<>();
		bookIds.add(4L);
		Mockito.when(restTemplate.postForEntity(uri, bookIds, List.class))
		.thenReturn(ResponseEntity.ok().build());
		ResponseEntity<?> result = userService.fetchAllSubscribedBooks(3L);
		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
	
	@Test
	void TestFetchAllSubscribedBooksForError() {
		Optional<User> user = Optional.ofNullable(new User());
		when(userRepository.findById(any())).thenReturn(user);
		
		ResponseEntity<?> result = userService.fetchAllSubscribedBooks(3L);
		assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
	}

	private Optional<User> getUser() {
		User user = new User();
		Set<Subscription> subs = new HashSet<>();
		Subscription sub = getSubscripton();
		subs.add(sub);
		user.setSubscriptions(subs);
		return Optional.ofNullable(user);
	}


	private Subscription getSubscripton() {
		Subscription sub = new Subscription();
		sub.setActive(true);
		sub.setBookId(4L);
		sub.setId(4L);
		sub.setSubscriptionTime("2022-11-01 16:19:00.100");
		sub.setUserId(3L);
		return sub;
	}

	private SubscriptionRequest getSubscriptonRequest() {
		SubscriptionRequest sub = new SubscriptionRequest();
		sub.setActive(true);
		sub.setBookId(4L);
		sub.setId(4L);
		sub.setSubscriptionTime("2022-11-01 16:19:00.100");
		sub.setUserId(3L);
		return sub;
	}
}
