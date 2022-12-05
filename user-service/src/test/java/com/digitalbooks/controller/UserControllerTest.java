package com.digitalbooks.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.digitalbooks.DigitalbooksUserApplication;
import com.digitalbooks.repositories.UserRepository;
import com.digitalbooks.userdetails.UserService;

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

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
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

	
	
}
