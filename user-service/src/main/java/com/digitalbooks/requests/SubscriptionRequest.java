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
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setSubscriptionTime(String subscriptionTime) {
		this.subscriptionTime = subscriptionTime;
	}

	@Override
	public String toString() {
		return "SubscriptionRequest [id=" + id + ", bookId=" + bookId + ", userId=" + userId + ", active=" + active
				+ ", subscriptionTime=" + subscriptionTime + "]";
	}

}
