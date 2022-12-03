package com.digitalbooks.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.digitalbooks.entities.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{

}
