package com.gestor_de_compras.brisa.repository;

import com.gestor_de_compras.brisa.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByLogin(String login);
    Optional<User> findByEmail(String email);

}