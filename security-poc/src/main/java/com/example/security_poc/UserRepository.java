package com.example.security_poc;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA vai criar a query segura para nós!
    User findByUsernameAndPassword(String username, String password);
}