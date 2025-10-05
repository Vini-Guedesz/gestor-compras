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

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    public List<UserDTO> findAll() {
        return repository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO findById(Integer id) {
        return repository.findById(id)
                .map(UserDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID:" + id));
    }

    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        if (repository.findByUsername(dto.username()) != null) {
            throw new DataIntegrityConflictException("Nome de usuário já cadastrado");
        }

        User user = new User(null, dto.username(), encoder.encode(dto.senha()), dto.role(), dto.email());
        User savedUser = repository.save(user);
        return new UserDTO(savedUser);
    }

    @Transactional
    public UserDTO update(UserUpdateDTO dto) {
        User user = repository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + dto.id()));

        updateUserData(user, dto);
        return new UserDTO(repository.save(user));
    }

    private void updateUserData(User user, UserUpdateDTO dto) {
        if (dto.username() != null) {
            user.setUsername(dto.username());
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

    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}