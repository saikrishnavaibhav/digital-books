package com.digitalbooks.userdetails;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digitalbooks.entities.Subscription;
import com.digitalbooks.entities.User;
import com.digitalbooks.repositories.SubscriptionRepository;
import com.digitalbooks.repositories.UserRepository;

@Component
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SubscriptionRepository subscriptionRepository;

	public Subscription verifyUserAndSubscription(Long userId, Long subscriptionId) {
		Optional<User> isUserAvailable = userRepository.findById(userId);

		if (isUserAvailable.isPresent()) {
			User user = isUserAvailable.get();
			Subscription subscription = user.getSubscriptions().stream()
					.filter(sub -> sub.getId().equals(subscriptionId)).findAny().orElse(null);
			if (subscription != null) {
				return subscription;
			} else {
				System.out.println("subscription not available");
			}
		} else {
			System.out.println("user not available");
		}
		
		return null;
	}
	
}
