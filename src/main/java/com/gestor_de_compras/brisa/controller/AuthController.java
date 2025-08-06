package com.gestor_de_compras.brisa.controller;

import com.gestor_de_compras.brisa.dto.LoginRequestDTO;
import com.gestor_de_compras.brisa.dto.TokenResponseDTO;
import com.gestor_de_compras.brisa.model.user.User;
import com.gestor_de_compras.brisa.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());

        var authentication = authManager.authenticate(authToken);
        var usuario = (User) authentication.getPrincipal();

        var jwt = jwtService.gerarToken(usuario);

        return ResponseEntity.ok(new TokenResponseDTO(jwt));
    }
}

