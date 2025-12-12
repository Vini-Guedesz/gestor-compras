package com.gestordecompras.gestorcomprasbackend.repository;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para gerenciamento de usuários do sistema.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Busca um usuário pelo nome completo.
     *
     * @param nome Nome do usuário
     * @return Entidade User encontrada (ou null)
     */
    User findByNome(String nome);  // Antes: findByUsername

    /**
     * Busca um usuário pelo e-mail (utilizado para autenticação).
     *
     * @param email E-mail do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmail(String email);  // Usado para autenticação

}