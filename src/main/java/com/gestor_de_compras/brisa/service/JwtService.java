package com.gestor_de_compras.brisa.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    //chave temporaria, apenas para testes.
    private String secretKey = "Flu4LtEYdwkMvQluZFC35vkzceE5ATWTWeyUCniMpP7JhNQsThH9w28x8dWPt5Q5vNFcXeOUTFZcnSKcTE71SjmuS9vixVP67UU8M2oBsy7yJNee0SHiRwb6cSdVHBlNduCKLDTfwYSJCtAaoQssspJ1oFllCLvbvrHUPQ9iUItsKlCogwKm6UTZp3DYVcy653U5m2ai3bstaLlB4MZIKWp1m7T8KDSkdPX4W8MLtHorDpnPmhXz3U5Khwpqf1GJ";
    private final SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    public String gerarToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public boolean tokenValido(String token, UserDetails userDetails) {
        return extrairUsername(token).equals(userDetails.getUsername());
    }
}