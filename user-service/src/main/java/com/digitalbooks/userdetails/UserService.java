package com.digitalbooks.userdetails;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

	public Subscription getSubscription(Long userId, Long subscriptionId) {

		Subscription subscription = getSubscriptions(userId).stream().filter(sub -> sub.getId().equals(subscriptionId))
				.findAny().orElse(null);
		if (subscription != null) {
			return subscription;
		}

		return null;
	}
	
	public Set<Subscription> getSubscriptions(Long userId) {
		
		Set<Subscription> subscriptionsList = new HashSet<>();
		User user = verifyAndGetUser(userId);
		if(user != null)
			subscriptionsList = user.getSubscriptions();

		return subscriptionsList;
	}

	public User verifyAndGetUser(Long userId) {
		Optional<User> isUserAvailable = userRepository.findById(userId);

		return isUserAvailable.isPresent() ? isUserAvailable.get() : null;
	}
}
