package com.gestordecompras.gestorcomprasbackend.config;

import com.gestordecompras.gestorcomprasbackend.model.user.User;
import com.gestordecompras.gestorcomprasbackend.model.user.UserRole;
import com.gestordecompras.gestorcomprasbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Verificar se já existem usuários
        if (userRepository.count() == 0) {
            
            // Criar usuário admin
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@gestor.com");
            admin.setSenha(encoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);

            // Criar usuário comum
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@gestor.com");
            user.setSenha(encoder.encode("user123"));
            user.setRole(UserRole.USER);
            userRepository.save(user);

            System.out.println("=== USUÁRIOS DE TESTE CRIADOS ===");
            System.out.println("Administrador:");
            System.out.println("  Email: admin@gestor.com");
            System.out.println("  Senha: admin123");
            System.out.println();
            System.out.println("Usuário comum:");
            System.out.println("  Email: user@gestor.com");
            System.out.println("  Senha: user123");
            System.out.println("=================================");
        }
    }
}
