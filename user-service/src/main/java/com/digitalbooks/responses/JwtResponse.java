package com.digitalbooks.responses;

import java.util.List;
import java.util.Set;

import com.digitalbooks.entities.Subscription;

public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private Long id;
	private String userName;
	private String emailId;
	private List<String> roles;
	private Set<Subscription> subscriptions;
	

	public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
		this.token = accessToken;
		this.id = id;
		this.userName = username;
		this.emailId = email;
		this.roles = roles;
	}

	public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles, Set<Subscription> subscriptions) {
		this.token = accessToken;
		this.id = id;
		this.userName = username;
		this.emailId = email;
		this.roles = roles;
		this.subscriptions = subscriptions;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return emailId;
	}

	public void setEmail(String email) {
		this.emailId = email;
	}

	public String getUsername() {
		return userName;
	}

	public void setUsername(String username) {
		this.userName = username;
	}

	public List<String> getRoles() {
		return roles;
	}
	
	public Set<Subscription> getSubscriptions(){
		return subscriptions;
	}
}
