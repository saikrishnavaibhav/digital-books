package com.digitalbooks.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digitalbooks.entities.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{

	Boolean existsByBookIdAndUserId(Long bookId, Long userId);
	
	List<Subscription> findByBookIdAndUserId(Long bookId, Long userId);
	
}
