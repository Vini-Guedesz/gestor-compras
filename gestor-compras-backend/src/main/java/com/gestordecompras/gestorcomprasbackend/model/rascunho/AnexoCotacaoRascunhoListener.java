package com.gestordecompras.gestorcomprasbackend.model.rascunho;

import com.gestordecompras.gestorcomprasbackend.model.storage.PdfStorage;
import com.gestordecompras.gestorcomprasbackend.repository.PdfStorageRepository;
import jakarta.persistence.PreRemove;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listener JPA para gerenciar ciclo de vida de AnexoCotacaoRascunho
 *
 * Responsável por:
 * - Decrementar contador de referências do PdfStorage quando anexo é deletado
 * - Remover PdfStorage órfão quando não há mais referências
 *
 * @since 3.0.0
 */
@Slf4j
@Component
public class AnexoCotacaoRascunhoListener {

    private static PdfStorageRepository pdfStorageRepository;

    @Autowired
    public void setPdfStorageRepository(PdfStorageRepository repository) {
        AnexoCotacaoRascunhoListener.pdfStorageRepository = repository;
    }

    /**
     * Executado ANTES de deletar o anexo
     * Decrementa contador de referências e remove PDF se órfão
     */
    @PreRemove
    public void preRemove(AnexoCotacaoRascunho anexo) {
        if (anexo.getPdfStorage() == null) {
            return; // Anexo usa arquitetura legada (conteudoLegacy), sem PdfStorage
        }

        PdfStorage pdfStorage = anexo.getPdfStorage();
        log.debug("Anexo {} sendo removido. PdfStorage: {} (refs: {})",
                anexo.getId(), pdfStorage.getId(), pdfStorage.getContadorReferencias());

        // Decrementar contador de referências
        boolean podeSerDeletado = pdfStorage.decrementarReferencias();
        pdfStorageRepository.save(pdfStorage);

        // Se não há mais referências, deletar o PDF físico
        if (podeSerDeletado) {
            log.info("PdfStorage {} órfão detectado (sem mais referências). Removendo PDF físico de {} bytes",
                    pdfStorage.getId(), pdfStorage.getTamanhoBytes());
            pdfStorageRepository.delete(pdfStorage);
        } else {
            log.debug("PdfStorage {} ainda tem {} referência(s). Mantendo PDF.",
                    pdfStorage.getId(), pdfStorage.getContadorReferencias());
        }
    }
}
