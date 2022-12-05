package com.digitalbooks.entities;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Subscription")
public class Subscription {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long bookId;
	
	@NotNull
	private Long userId;
	private boolean active = true;
	
	//@Temporal(TemporalType.TIMESTAMP)
	private Timestamp subscriptionTime;

	public Subscription() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Timestamp getSubscriptionTime() {
		return subscriptionTime;
	}

	public void setSubscriptionTime(String subscriptionTime) {
		this.subscriptionTime = Timestamp.valueOf(subscriptionTime);
	}

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", bookId=" + bookId + ", userId=" + userId + ", active=" + active
				+ ", subscriptionTime=" + subscriptionTime + "]";
	}
	
}
