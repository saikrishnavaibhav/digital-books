package com.digitalbooks.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.digitalbooks.DigitalbooksUserApplication;
import com.digitalbooks.repositories.SubscriptionRepository;
import com.digitalbooks.repositories.UserRepository;
import com.digitalbooks.userdetails.UserService;
import com.digitalbooks.utils.UserUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = { DigitalbooksUserApplication.class })
@WebAppConfiguration
class UserControllerTest {

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

	//private MockRestServiceServer mockServer;
	
	@BeforeEach
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
		//this.mockServer = MockRestServiceServer.bindTo(restTemplate).build();
		//mockServer = MockRestServiceServer.createServer(restTemplate);
		//this.restTemplate = Mockito.mock(TestRestTemplate.class);
	}
	
	@Test
	void testRegisterUser() throws Exception {
		
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
	void testRegisterUserAsReader() throws Exception {
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
	void testRegisterUserAsAuthor() throws Exception {
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
	void testRegisterUserForExistingUserName() throws Exception {
		
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
	void testRegisterUserForExistingEmailId() throws Exception {
		
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
				   .andExpect(jsonPath("$.message").value(UserUtils.EMAILID_ALREADY_TAKEN));
	}
	
	@Test
	void testRegisterUserForExistingPhoneNumber() throws Exception {
		
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
				   .andExpect(jsonPath("$.message").value(UserUtils.PHONENUMBER_ALREADY_TAKEN));
	}

	@WithMockUser(roles="READER")
	@Test
	void testSubscribeABook() throws Exception {
		
		when(userService.subscribeABook(any(), anyLong())).thenReturn(ResponseEntity.ok().build());
		
		mockMvc.perform(post("/api/v1/digitalbooks/{book-id}/subscribe",2)
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
				   		+ "    \"bookId\": 2,\r\n"
				   		+ "	\"userId\": 3,\r\n"
				   		+ "	\"active\": true,\r\n"
				   		+ "	\"subscriptionTime\": \"2022-12-04 16:19:00.100\"\r\n"
				   		+ "}")						
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(roles="READER")
	@Test
	void testSubscribeABookForError() throws Exception {
		
		when(userService.subscribeABook(any(), anyLong())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(post("/api/v1/digitalbooks/{book-id}/subscribe",0)
				   .contentType(MediaType.APPLICATION_JSON)
				   .content("{\r\n"
				   		+ "    \"bookId\": 2,\r\n"
				   		+ "	\"userId\": 3,\r\n"
				   		+ "	\"active\": true,\r\n"
				   		+ "	\"subscriptionTime\": \"2022-12-04 16:19:00.100\"\r\n"
				   		+ "}")						
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="READER")
	@Test
	void testFetchSusbcribedBook() throws Exception {
		when(userRepository.existsById(2L)).thenReturn(true);
		when(subscriptionRepository.existsById(2L)).thenReturn(true);
		when(userService.fetchSubscribedBook(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());
		
		mockMvc.perform(get("/api/v1/digitalbooks/readers/{user-id}/books/{subscription-id}",2,2)						
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(roles="READER")
	@Test
	void testFetchSusbcribedBookForError() throws Exception {
		when(userRepository.existsById(2L)).thenReturn(false);
		when(subscriptionRepository.existsById(2L)).thenReturn(false);
		when(userService.fetchSubscribedBook(anyLong(), anyLong())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(get("/api/v1/digitalbooks/readers/{user-id}/books/{subscription-id}",2,2)						
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="READER")
	@Test
	void testFetchSusbcribedBookForSubscriptionError() throws Exception {
		when(userRepository.existsById(2L)).thenReturn(true);
		when(subscriptionRepository.existsById(2L)).thenReturn(false);
		when(userService.fetchSubscribedBook(anyLong(), anyLong())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(get("/api/v1/digitalbooks/readers/{user-id}/books/{subscription-id}",2,2)						
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="READER")
	@Test
	void testFetchAllSusbcribeABooks() throws Exception {
		
		when(userService.fetchAllSubscribedBooks(anyLong())).thenReturn(ResponseEntity.ok().build());
		
		mockMvc.perform(get("/api/v1/digitalbooks/readers/{user-id}/books",2)					
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(roles="READER")
	@Test
	void testFetchAllSusbcribeABooksForError() throws Exception {
		
		when(userService.fetchAllSubscribedBooks(anyLong())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(get("/api/v1/digitalbooks/readers/{user-id}/books",0L)					
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="AUTHOR")
	@Test
	void testBlockABook() throws Exception {
		
		when(userService.blockBook(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.ok().build());
		
		mockMvc.perform(post("/api/v1/digitalbooks/author/{author-id}/books/{book-id}",2,2)
				.queryParam("block", "true")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(roles="AUTHOR")
	@Test
	void testBlockABookForAuthorError() throws Exception {
		
		when(userService.blockBook(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(post("/api/v1/digitalbooks/author/{author-id}/books/{book-id}",0,2)
				.queryParam("block", "true")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="AUTHOR")
	@Test
	void testBlockABookForBookError() throws Exception {
		
		when(userService.blockBook(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(post("/api/v1/digitalbooks/author/{author-id}/books/{book-id}",2,0)
				.queryParam("block", "true")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="AUTHOR")
	@Test
	void testUpdateABook() throws Exception {
		
		when(userService.updateBook(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok().build());
		
		mockMvc.perform(put("/api/v1/digitalbooks/author/{author-id}/updateBook/{book-id}",2,2)
				.contentType(MediaType.APPLICATION_JSON)
			   .content("{\r\n"
			   		+ "        \"id\": 1,\r\n"
			   		+ "        \"logo\": \"https://m.media-amazon.com/images/W/WEBP_402378-T1/images/I/5163N91r6lL._SY300_.jpg\",\r\n"
			   		+ "        \"title\": \"A Game of Thrones (A Song of Ice and Fire, Book 1)\",\r\n"
			   		+ "        \"category\": \"FICTION\",\r\n"
			   		+ "        \"price\": 499,\r\n"
			   		+ "        \"authorId\": 1,\r\n"
			   		+ "        \"authorName\": \"RRMARTIN\",\r\n"
			   		+ "        \"publisher\": \"Bantam, Media tie-in\",\r\n"
			   		+ "        \"publishedDate\": \"2022-12-16T07:25:20.000+00:00\",\r\n"
			   		+ "        \"content\": \"winter.\",\r\n"
			   		+ "        \"active\": true\r\n"
			   		+ "    }")	
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(roles="AUTHOR")
	@Test
	void testUpdateABookForAuthorError() throws Exception {
		
		when(userService.updateBook(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(put("/api/v1/digitalbooks/author/{author-id}/updateBook/{book-id}",0,2)
				.contentType(MediaType.APPLICATION_JSON)
			   .content("{\r\n"
			   		+ "        \"id\": 1,\r\n"
			   		+ "        \"logo\": \"https://m.media-amazon.com/images/W/WEBP_402378-T1/images/I/5163N91r6lL._SY300_.jpg\",\r\n"
			   		+ "        \"title\": \"A Game of Thrones (A Song of Ice and Fire, Book 1)\",\r\n"
			   		+ "        \"category\": \"FICTION\",\r\n"
			   		+ "        \"price\": 499,\r\n"
			   		+ "        \"authorId\": 1,\r\n"
			   		+ "        \"authorName\": \"RRMARTIN\",\r\n"
			   		+ "        \"publisher\": \"Bantam, Media tie-in\",\r\n"
			   		+ "        \"publishedDate\": \"2022-12-16T07:25:20.000+00:00\",\r\n"
			   		+ "        \"content\": \"winter.\",\r\n"
			   		+ "        \"active\": true\r\n"
			   		+ "    }")	
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="AUTHOR")
	@Test
	void testUpdateABookForBookError() throws Exception {
		
		when(userService.updateBook(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(put("/api/v1/digitalbooks/author/{author-id}/updateBook/{book-id}",2,0)
				.contentType(MediaType.APPLICATION_JSON)
			   .content("{\r\n"
			   		+ "        \"id\": 1,\r\n"
			   		+ "        \"logo\": \"https://m.media-amazon.com/images/W/WEBP_402378-T1/images/I/5163N91r6lL._SY300_.jpg\",\r\n"
			   		+ "        \"title\": \"A Game of Thrones (A Song of Ice and Fire, Book 1)\",\r\n"
			   		+ "        \"category\": \"FICTION\",\r\n"
			   		+ "        \"price\": 499,\r\n"
			   		+ "        \"authorId\": 1,\r\n"
			   		+ "        \"authorName\": \"RRMARTIN\",\r\n"
			   		+ "        \"publisher\": \"Bantam, Media tie-in\",\r\n"
			   		+ "        \"publishedDate\": \"2022-12-16T07:25:20.000+00:00\",\r\n"
			   		+ "        \"content\": \"winter.\",\r\n"
			   		+ "        \"active\": true\r\n"
			   		+ "    }")	
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="AUTHOR")
	@Test
	void testGetAuthorBooks() throws Exception {
		
		when(userService.blockBook(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.ok().build());
		
		mockMvc.perform(get("/api/v1/digitalbooks/author/{author-id}/getAllBooks",2)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(roles="AUTHOR")
	@Test
	void testGetAuthorBooksForError() throws Exception {
		
		when(userService.blockBook(anyLong(), anyLong(), anyBoolean())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(get("/api/v1/digitalbooks/author/{author-id}/getAllBooks",0)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="READER")
	@Test
	void testCancelSubscription() throws Exception {
		
		when(userService.cancelSubscription(any(), anyLong())).thenReturn(ResponseEntity.ok().build());
		
		mockMvc.perform(post("/api/v1/digitalbooks/readers/{user-id}/books/{subscription-id}/cancel-subscription",2,2)						
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
	@WithMockUser(roles="READER")
	@Test
	void testCancelSubscriptionForUserError() throws Exception {
		
		when(userService.cancelSubscription(any(), anyLong())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(post("/api/v1/digitalbooks/readers/{user-id}/books/{subscription-id}/cancel-subscription",0,2)						
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@WithMockUser(roles="READER")
	@Test
	void testCancelSubscriptionForSubscriptionError() throws Exception {
		
		when(userService.cancelSubscription(any(), anyLong())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(post("/api/v1/digitalbooks/readers/{user-id}/books/{subscription-id}/cancel-subscription",2,0)						
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}
	
	@Test
	void testSearchBooksForBadRequest() throws Exception {
		
		when(userService.searchBooks(any(), any(), any())).thenReturn(ResponseEntity.badRequest().build());
		
		mockMvc.perform(get("/api/v1/digitalbooks/search")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@Test
	void testSearchBooks() throws Exception {
		
		when(userService.searchBooks(anyString(), anyString(), anyString())).thenReturn(ResponseEntity.ok().build());
		
		mockMvc.perform(get("/api/v1/digitalbooks/search")
				.param("category", "category")
				.param("title", "title")
				.param("author", "author")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
	
}
