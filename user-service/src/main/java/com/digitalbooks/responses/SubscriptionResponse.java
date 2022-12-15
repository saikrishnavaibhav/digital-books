package com.digitalbooks.responses;

import java.sql.Timestamp;
import java.util.Objects;

public class SubscriptionResponse {

	private Long id;
	
	private Timestamp subscriptionTime;

	
	
	public SubscriptionResponse(Long id, Timestamp subscriptionTime) {
		this.id = id;
		this.subscriptionTime = subscriptionTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getSubscriptionTime() {
		return subscriptionTime;
	}

	public void setSubscriptionTime(Timestamp subscriptionTime) {
		this.subscriptionTime = subscriptionTime;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, subscriptionTime);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubscriptionResponse other = (SubscriptionResponse) obj;
		return Objects.equals(id, other.id) && Objects.equals(subscriptionTime, other.subscriptionTime);
	}

	@Override
	public String toString() {
		return "SubscriptionResponse [id=" + id + ", subscriptionTime=" + subscriptionTime + "]";
	}
	
}
