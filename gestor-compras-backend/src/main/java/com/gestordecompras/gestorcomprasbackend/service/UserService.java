package com.gestordecompras.gestorcomprasbackend.service;

import com.gestordecompras.gestorcomprasbackend.dto.user.UpdateUserRoleDTO;
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

import java.time.LocalDateTime;
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

        User user = new User(null, dto.nome(), encoder.encode(dto.senha()), dto.role(), dto.email(),
                             dto.ativo() != null ? dto.ativo() : true, LocalDateTime.now());
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
        user.setLastModifiedAt(LocalDateTime.now());
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
     * Desativa um usuário no sistema (soft delete).
     *
     * <p>Ao invés de excluir permanentemente, o usuário é marcado como inativo.
     * Usuários inativos não podem fazer login no sistema.</p>
     *
     * <p><b>IMPORTANTE:</b> O usuário continua existindo no banco de dados,
     * mas não pode mais acessar o sistema. Para reativar, use {@link #reactivateUser(Integer)}.</p>
     *
     * @param id ID do usuário a ser desativado
     * @throws EntityNotFoundException se usuário não encontrado
     */
    public void deleteUser(Integer id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        user.setAtivo(false);
        user.setLastModifiedAt(LocalDateTime.now());
        repository.save(user);
    }

    /**
     * Atualiza a role de um usuário (exclusivo para ADMINs).
     *
     * <p>Este método permite que administradores alterem o nível de acesso
     * de outros usuários no sistema.</p>
     *
     * <p><b>Roles disponíveis:</b></p>
     * <ul>
     *   <li>ADMIN - Acesso total ao sistema</li>
     *   <li>USUARIO - Acesso básico</li>
     *   <li>COMPRADOR - Gerenciamento de pedidos de compra</li>
     *   <li>APROVADOR - Aprovação de pedidos de compra</li>
     * </ul>
     *
     * @param dto DTO contendo ID do usuário e nova role
     * @return UserDTO atualizado com a nova role
     * @throws EntityNotFoundException se usuário não encontrado
     */
    public UserDTO updateUserRole(UpdateUserRoleDTO dto) {
        User user = repository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + dto.userId()));

        user.setRole(dto.newRole());
        user.setLastModifiedAt(LocalDateTime.now());
        return new UserDTO(repository.save(user));
    }

    /**
     * Reativa um usuário previamente desativado.
     *
     * <p>Permite que usuários desativados voltem a ter acesso ao sistema.
     * Apenas ADMINs podem reativar usuários.</p>
     *
     * @param id ID do usuário a ser reativado
     * @return UserDTO do usuário reativado
     * @throws EntityNotFoundException se usuário não encontrado
     */
    public UserDTO reactivateUser(Integer id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));

        user.setAtivo(true);
        user.setLastModifiedAt(LocalDateTime.now());
        return new UserDTO(repository.save(user));
    }
}