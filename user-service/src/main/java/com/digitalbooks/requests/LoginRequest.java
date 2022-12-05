package com.digitalbooks.requests;

import javax.validation.constraints.NotBlank;

public class LoginRequest {
	
	@NotBlank(message = "Enter valid userName")
	private String userName;

	@NotBlank(message = "Enter valid password")
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
