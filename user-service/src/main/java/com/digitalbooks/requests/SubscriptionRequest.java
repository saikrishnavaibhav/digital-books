package com.digitalbooks.requests;

public class SubscriptionRequest {

	private Long id;
	
	private Long bookId;
	
	private Long userId;
	
	private boolean active = true;
	
	private String subscriptionTime;

	public Long getId() {
		return id;
	}

	public Long getBookId() {
		return bookId;
	}

	public Long getUserId() {
		return userId;
	}

	public boolean isActive() {
		return active;
	}

	public String getSubscriptionTime() {
		return subscriptionTime;
	}

	@Override
	public String toString() {
		return "SubscriptionRequest [id=" + id + ", bookId=" + bookId + ", userId=" + userId + ", active=" + active
				+ ", subscriptionTime=" + subscriptionTime + "]";
	}

}
