package com.gestor_de_compras.brisa.service;

import com.gestor_de_compras.brisa.dto.UserCreateDTO;
import com.gestor_de_compras.brisa.dto.UserDTO;
import com.gestor_de_compras.brisa.dto.UserUpdateDTO;
import com.gestor_de_compras.brisa.model.user.User;
import com.gestor_de_compras.brisa.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    PasswordEncoder encoder;

    public List<UserDTO> findAll() {
        return repository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO findById(Integer id) {
        return repository.findById(id)
                .map(UserDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        if (repository.findByLogin(dto.login()) != null) {
            throw new IllegalArgumentException("Login já cadastrado");
        }

        User user = new User(null, dto.login(), encoder.encode(dto.senha()), dto.role(), dto.email());
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
        if (dto.login() != null) {
            user.setLogin(dto.login());
        }
        if (dto.senha() != null) {
            user.setSenha(dto.senha());
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