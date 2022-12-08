package com.digitalbooks.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.digitalbooks.DigitalbooksUserApplication;
import com.digitalbooks.entities.Subscription;
import com.digitalbooks.entities.User;
import com.digitalbooks.repositories.SubscriptionRepository;
import com.digitalbooks.repositories.UserRepository;
import com.digitalbooks.responses.BookResponse;
import com.digitalbooks.userdetails.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = { DigitalbooksUserApplication.class })
@WebAppConfiguration
public class UserControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	UserService userService;

	@MockBean
	UserRepository userRepository;
	
	@MockBean
	SubscriptionRepository subscriptionRepository;
	
	@Autowired
	RestTemplate restTemplate;
	
	private MockMvc mockMvc;

	private MockRestServiceServer mockServer;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@BeforeEach
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
		this.mockServer = MockRestServiceServer.bindTo(restTemplate).build();
		//mockServer = MockRestServiceServer.createServer(restTemplate);
		//this.restTemplate = Mockito.mock(TestRestTemplate.class);
	}
	
	@Test
	public void testRegisterUser() throws Exception {
		
		when(userRepository.save(any())).thenReturn(null);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\""
					   		+ "}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isOk())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("User registered successfully!"));
	}

	@Test
	public void testRegisterUserAsReader() throws Exception {
		when(userRepository.save(any())).thenReturn(null);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\",\r\n"
					   		+ "    \"role\": [\r\n"
					   		+ "        \"reader\"\r\n"
					   		+ "    ]}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isOk())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("User registered successfully!"));
	}
	
	@Test
	public void testRegisterUserAsAuthor() throws Exception {
		when(userRepository.save(any())).thenReturn(null);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\",\r\n"
					   		+ "    \"role\": [\r\n"
					   		+ "        \"author\"\r\n"
					   		+ "    ]}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isOk())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("User registered successfully!"));
	}
	
	@Test
	public void testRegisterUserForExistingUserName() throws Exception {
		
		when(userRepository.existsByUserName(any())).thenReturn(true);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\""
					   		+ "}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isBadRequest())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("Error: UserName is already taken!"));
	}
	
	@Test
	public void testRegisterUserForExistingEmailId() throws Exception {
		
		when(userRepository.existsByEmailId(any())).thenReturn(true);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\""
					   		+ "}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isBadRequest())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("Error: EmailId is already in use!"));
	}
	
	@Test
	public void testRegisterUserForExistingPhoneNumber() throws Exception {
		
		when(userRepository.existsByPhoneNumber(any())).thenReturn(true);
		
		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
					   		+ "    \"userName\": \"sample\",\r\n"
					   		+ "    \"emailId\": \"sample@gmail.com\",\r\n"
					   		+ "    \"password\": \"Password@135\",\r\n"
					   		+ "    \"phoneNumber\": \"1234567890\""
					   		+ "}")						
				   .accept(MediaType.APPLICATION_JSON))
				   .andExpect(status().isBadRequest())
				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
				   .andExpect(jsonPath("$.message").value("Error: PhoneNumber is already in use!"));
	}
	
	public void testAuthenticateUser() throws Exception {
		
//		when(userRepository.save(any())).thenReturn(null);
//		
//		mockMvc.perform(post("/api/v1/digitalbooks/sign-up")
//				   .contentType(MediaType.APPLICATION_JSON)
//				   .content("{\r\n"
//				   		+ "    \"userName\": \"robin\",\r\n"
//				   		+ "    \"password\": \"Password@123\"\r\n"
//				   		+ "}")						
//				   .accept(MediaType.APPLICATION_JSON))
//				   .andExpect(status().isOk())
//				   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				   .andExpect(jsonPath("$.message").value("User registered successfully!"));
	}

	@WithMockUser(roles="READER")
	@Test
	public void testSusbcribeABook() throws Exception {
	
		Optional<User> user = Optional.ofNullable(new User());
		when(userRepository.findById(any())).thenReturn(user);
		
		User user1 = new User();
		Set<Subscription> subs = new HashSet<>();
		Subscription sub = getSubscripton();
		subs.add(sub);
		user1.setSubscriptions(subs);
		
		when(userRepository.save(any())).thenReturn(user1);
		
		URI uri = new URI("http://localhost:8082/api/v1/digitalbooks/book/" + sub.getBookId() + "/checkBook");
		
		//MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		when(restTemplate.getForObject(uri, Boolean.class)).thenReturn(true);
		mockServer.expect(requestTo(uri)).andExpect(method(HttpMethod.GET))
				     .andRespond(withStatus(HttpStatus.OK).body("true"));
		mockServer.verify();
		
		mockMvc.perform(post("/api/v1/digitalbooks/{book-id}/subscribe",2)
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
				   		+ "    \"bookId\": 2,\r\n"
				   		+ "	\"userId\": 3,\r\n"
				   		+ "	\"active\": true,\r\n"
				   		+ "	\"subscriptionTime\": \"2022-12-04 16:19:00.100\"\r\n"
				   		+ "}")						
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.subscriptions[0].bookId").value(2))
				.andExpect(jsonPath("$.subscriptions[0].userId").value(3))
				.andExpect(jsonPath("$.subscriptions[0].active").value(true));
	}
	

	private BookResponse getBookResponse() {
		BookResponse bookResponse = new BookResponse();
		bookResponse.setActive(true);
		bookResponse.setAuthorId(1L);
		bookResponse.setAuthorName("oda");
		bookResponse.setTitle("one piece");
		return bookResponse;
	}

	private Subscription getSubscripton() {
		Subscription sub = new Subscription();
		sub.setActive(true);
		sub.setBookId(4L);
		sub.setId(4L);
		sub.setSubscriptionTime("2022-12-04 16:19:00.100");
		sub.setUserId(3L);
		return sub;
	}
}
