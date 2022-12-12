package com.digitalbooks.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.ObjectUtils;

@Entity
@Table(name = "Subscription")
public class Subscription implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long bookId;
	
	@NotNull
	private Long userId;
	
	private boolean active = true;
	
	private Timestamp subscriptionTime;

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
		
		this.subscriptionTime = ObjectUtils.isEmpty(subscriptionTime) ? Timestamp.valueOf(LocalDateTime.now()) : Timestamp.valueOf(subscriptionTime);
	}

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", bookId=" + bookId + ", userId=" + userId + ", active=" + active
				+ ", subscriptionTime=" + subscriptionTime + "]";
	}
	
}
