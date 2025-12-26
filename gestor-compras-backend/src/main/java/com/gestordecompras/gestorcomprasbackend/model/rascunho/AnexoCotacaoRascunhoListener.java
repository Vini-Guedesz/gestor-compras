package com.gestordecompras.gestorcomprasbackend.model.rascunho;

import com.gestordecompras.gestorcomprasbackend.model.storage.PdfStorage;
import com.gestordecompras.gestorcomprasbackend.repository.PdfStorageRepository;
import jakarta.persistence.PreRemove;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Listener JPA para gerenciar ciclo de vida de AnexoCotacaoRascunho
 *
 * Responsável por:
 * - Decrementar contador de referências do PdfStorage quando anexo é deletado
 * - Remover PdfStorage órfão quando não há mais referências
 *
 * IMPORTANTE: Entity Listeners não são gerenciados pelo Spring por padrão.
 * Usamos ApplicationContextAware para obter beans do Spring manualmente.
 *
 * @since 3.0.0
 */
@Slf4j
@Component
public class AnexoCotacaoRascunhoListener implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        AnexoCotacaoRascunhoListener.applicationContext = context;
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

        // Obter repository do Spring Context (Entity Listeners não são injetados automaticamente)
        if (applicationContext == null) {
            log.warn("ApplicationContext não inicializado. Não foi possível limpar PdfStorage órfão do anexo {}", anexo.getId());
            return;
        }

        PdfStorageRepository pdfStorageRepository = applicationContext.getBean(PdfStorageRepository.class);
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
