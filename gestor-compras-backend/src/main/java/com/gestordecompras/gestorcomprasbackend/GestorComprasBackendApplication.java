package com.gestordecompras.gestorcomprasbackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry // Bug Fix #10: Habilitar suporte a @Retryable
public class GestorComprasBackendApplication {

	public static void main(String[] args) {
		loadEnvVariables();
		SpringApplication.run(GestorComprasBackendApplication.class, args);
	}

	private static void loadEnvVariables() {
		// Tenta carregar do diretorio atual e do diretorio pai para cobrir diferentes contextos de execucao
		loadDotenv("./");
		loadDotenv("../");
	}

	private static void loadDotenv(String directory) {
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory(directory)
					.ignoreIfMissing()
					.load();

			dotenv.entries().forEach(entry -> {
				if (System.getProperty(entry.getKey()) == null) {
					System.setProperty(entry.getKey(), entry.getValue());
				}
			});
		} catch (Exception e) {
			// Ignore silenciosamente se o arquivo .env nao for encontrado neste diretorio especifico.
		}
	}

}