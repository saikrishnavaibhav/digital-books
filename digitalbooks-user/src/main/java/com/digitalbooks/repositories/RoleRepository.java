package com.digitalbooks.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitalbooks.entities.Role;
import com.digitalbooks.entities.Roles;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

	Optional<Role> findByRole(Roles role);
	
}
