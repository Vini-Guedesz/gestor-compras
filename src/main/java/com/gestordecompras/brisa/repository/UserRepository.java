package com.gestordecompras.brisa.repository;

import com.gestordecompras.brisa.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    Optional<User> findByEmail(String email);

}