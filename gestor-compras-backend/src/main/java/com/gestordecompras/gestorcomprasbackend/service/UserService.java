package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.user.UserCreateDTO;
import com.gestordecompras.gestorcomprasbackend.dto.user.UserDTO;
import com.gestordecompras.gestorcomprasbackend.dto.user.UserUpdateDTO;
import com.gestordecompras.gestorcomprasbackend.exception.DataIntegrityConflictException;
import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de lógica de negócio para Usuários.
 *
 * <p>Responsável por:</p>
 * <ul>
 *   <li>Gerenciamento de usuários (CRUD)</li>
 *   <li>Criptografia de senhas com BCrypt</li>
 *   <li>Validação de duplicidade de nome/email</li>
 *   <li>Conversão entre entidades e DTOs</li>
 * </ul>
 *
 * <p><b>Transações:</b> Métodos de escrita são transacionais.
 * Em caso de erro, toda operação é revertida (rollback automático).</p>
 *
 * @since 1.0.0
 * @author Equipe de Desenvolvimento
 * @see User
 * @see UserDTO
 * @see UserRepository
 */
@Service
@Transactional
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    /**
     * Construtor com injeção de dependências.
     *
     * @param repository Repository JPA para usuários
     * @param encoder Encoder BCrypt para criptografia de senhas
     */
    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    /**
     * Lista todos os usuários cadastrados no sistema.
     *
     * <p>Retorna uma lista com todos os usuários, incluindo informações
     * básicas (ID, nome, email, role). Senhas não são incluídas no DTO.</p>
     *
     * @return Lista de UserDTO com dados de todos os usuários
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return repository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca um usuário específico por ID.
     *
     * @param id ID do usuário
     * @return UserDTO com dados do usuário
     * @throws EntityNotFoundException se usuário não encontrado com o ID fornecido
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Integer id) {
        return repository.findById(id)
                .map(UserDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    /**
     * Cria um novo usuário com senha criptografada.
     *
     * <p><b>Validações realizadas:</b></p>
     * <ul>
     *   <li>Nome único (não pode duplicar)</li>
     *   <li>Email formato válido (validado no DTO)</li>
     *   <li>Senha criptografada com BCrypt (força 10)</li>
     * </ul>
     *
     * @param dto Dados do novo usuário (nome, email, senha, role)
     * @return UserDTO com dados do usuário criado incluindo ID gerado
     * @throws DataIntegrityConflictException se nome já cadastrado
     */
    public UserDTO createUser(UserCreateDTO dto) {
        if (repository.findByNome(dto.nome()) != null) {
            throw new DataIntegrityConflictException("Nome já cadastrado");
        }

        User user = new User(null, dto.nome(), encoder.encode(dto.senha()), dto.role(), dto.email());
        User savedUser = repository.save(user);
        return new UserDTO(savedUser);
    }

    /**
     * Atualiza um usuário existente com merge parcial.
     *
     * <p>Apenas campos não-nulos do DTO serão atualizados.
     * Campos omitidos mantêm seus valores originais.</p>
     *
     * <p><b>Campos atualizáveis:</b></p>
     * <ul>
     *   <li>Nome</li>
     *   <li>Email</li>
     *   <li>Senha (re-criptografada automaticamente)</li>
     *   <li>Role</li>
     * </ul>
     *
     * @param dto Dados de atualização (ID obrigatório, demais campos opcionais)
     * @return UserDTO atualizado
     * @throws EntityNotFoundException se usuário não encontrado
     */
    public UserDTO updateUser(UserUpdateDTO dto) {
        User user = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + dto.id()));

        updateUserData(user, dto);
        return new UserDTO(repository.save(user));
    }

    /**
     * Atualiza dados do usuário com valores do DTO (merge parcial).
     *
     * <p>Apenas campos não-nulos são aplicados. Senha é re-criptografada se fornecida.</p>
     *
     * @param user Entidade usuário a ser atualizada
     * @param dto DTO com novos dados
     */
    private void updateUserData(User user, UserUpdateDTO dto) {
        if (dto.nome() != null) {
            user.setNome(dto.nome());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            user.setSenha(encoder.encode(dto.senha()));
        }
        if (dto.role() != null) {
            user.setRole(dto.role());
        }
        if (dto.email() != null){
            user.setEmail(dto.email());
        }
    }

    /**
     * Remove um usuário permanentemente do sistema.
     *
     * <p>Esta operação não pode ser desfeita.</p>
     *
     * @param id ID do usuário a ser removido
     * @throws EntityNotFoundException se usuário não encontrado
     */
    public void deleteUser(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}