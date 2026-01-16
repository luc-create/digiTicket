package com.gigiTicket.backend.repository;

import com.gigiTicket.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	java.util.Optional<User> findByEmail(String email);
	boolean existsByEmail(String email);
	java.util.List<User> findByActiveTrue();
}

