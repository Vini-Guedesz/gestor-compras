package com.gestordecompras.gestorcomprasbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry // Bug Fix #10: Habilitar suporte a @Retryable
public class GestorComprasBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestorComprasBackendApplication.class, args);
	}

}
