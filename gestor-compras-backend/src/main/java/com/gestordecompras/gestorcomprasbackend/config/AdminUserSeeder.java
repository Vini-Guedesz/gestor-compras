package com.gestordecompras.gestorcomprasbackend.config;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;
import com.gestordecompras.gestorcomprasbackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@Slf4j
public class AdminUserSeeder {

    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Nenhum usuário encontrado. Criando usuário ADMIN padrão...");

                User admin = new User();
                admin.setNome("Administrador Padrão");
                admin.setEmail("admin@admin.com");
                admin.setSenha(passwordEncoder.encode("admin"));
                admin.setRole(UserRole.ADMIN);
                admin.setAtivo(true);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setLastModifiedAt(LocalDateTime.now());

                userRepository.save(admin);

                log.info("=================================================");
                log.info("USUÁRIO ADMIN CRIADO COM SUCESSO");
                log.info("Email: admin@admin.com");
                log.info("Senha: admin");
                log.info("=================================================");
            } else {
                log.info("Usuários já existentes. Pular criação de admin padrão.");
            }
        };
    }
}
