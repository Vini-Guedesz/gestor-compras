package com.gestordecompras.gestorcomprasbackend.service;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Serviço para calcular hash SHA-256 de PDFs
 * Usado para deduplificação - evita armazenar o mesmo PDF múltiplas vezes
 */
@Service
public class PdfHashService {

    /**
     * Calcula o hash SHA-256 de um array de bytes
     *
     * @param content Conteúdo do PDF em bytes
     * @return Hash SHA-256 em formato hexadecimal (64 caracteres)
     */
    public String calculateSHA256(byte[] content) {
        if (content == null || content.length == 0) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content);
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao calcular hash SHA-256: algoritmo não disponível", e);
        }
    }

    /**
     * Converte array de bytes para representação hexadecimal
     *
     * @param bytes Array de bytes
     * @return String hexadecimal
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Verifica se dois PDFs são idênticos comparando seus hashes
     *
     * @param pdf1 Primeiro PDF
     * @param pdf2 Segundo PDF
     * @return true se os PDFs têm o mesmo conteúdo
     */
    public boolean areIdentical(byte[] pdf1, byte[] pdf2) {
        if (pdf1 == null || pdf2 == null) {
            return false;
        }

        String hash1 = calculateSHA256(pdf1);
        String hash2 = calculateSHA256(pdf2);

        return hash1 != null && hash1.equals(hash2);
    }
}
