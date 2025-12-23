package com.gestordecompras.gestorcomprasbackend.controller;

import com.gestordecompras.gestorcomprasbackend.config.ApiVersionConfig;
import com.gestordecompras.gestorcomprasbackend.dto.pedido.AcaoPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.pedido.DevolverPedidoDTO;
import com.gestordecompras.gestorcomprasbackend.dto.solicitacaodepedido.SolicitacaoDePedidoDTO;
import com.gestordecompras.gestorcomprasbackend.service.SolicitacaoDePedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciar Solicitações de Pedido.
 *
 * <p>Oferece operações CRUD completas com paginação para solicitações de pedido.
 * Cada solicitação contém múltiplos itens e pode ter várias cotações associadas.</p>
 *
 * <p><b>Endpoints principais:</b></p>
 * <ul>
 *   <li>GET /api/v1/solicitacoes-pedido - Listar com paginação</li>
 *   <li>GET /api/v1/solicitacoes-pedido/{id} - Buscar por ID</li>
 *   <li>POST /api/v1/solicitacoes-pedido - Criar nova solicitação</li>
 *   <li>PUT /api/v1/solicitacoes-pedido/{id} - Atualizar solicitação</li>
 *   <li>DELETE /api/v1/solicitacoes-pedido/{id} - Deletar solicitação</li>
 * </ul>
 *
 * <p><b>Autenticação:</b> JWT obrigatório</p>
 * <p><b>Roles permitidas:</b> USER, ADMIN</p>
 *
 * <p><b>Paginação:</b> Padrão 20 itens por página, ordenado por ID</p>
 *
 * @since 1.0.0
 * @author Equipe de Desenvolvimento
 * @see SolicitacaoDePedidoService
 * @see SolicitacaoDePedidoDTO
 */
@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/solicitacoes-pedido")
@Tag(name = "Solicitações de Pedido", description = "API para gerenciamento de solicitações de pedido (v1)")
@SecurityRequirement(name = "bearerAuth")
public class SolicitacaoDePedidoController {

    private final SolicitacaoDePedidoService solicitacaoDePedidoService;

    /**
     * Construtor com injeção de dependência do service.
     *
     * @param solicitacaoDePedidoService Service de lógica de negócio
     */
    public SolicitacaoDePedidoController(SolicitacaoDePedidoService solicitacaoDePedidoService) {
        this.solicitacaoDePedidoService = solicitacaoDePedidoService;
    }

    /**
     * Lista todas as solicitações de pedido com paginação.
     *
     * <p>Retorna uma página contendo solicitações ordenadas por ID.
     * Cada solicitação inclui itens e cotações associadas.</p>
     *
     * <p><b>Parâmetros de paginação:</b></p>
     * <ul>
     *   <li>page - Número da página (0-indexed, padrão: 0)</li>
     *   <li>size - Tamanho da página (padrão: 20)</li>
     *   <li>sort - Campo de ordenação (padrão: id)</li>
     * </ul>
     *
     * @param pageable Configuração de paginação e ordenação
     * @return ResponseEntity contendo Page de SolicitacaoDePedidoDTO
     */
    @GetMapping
    @Operation(summary = "Listar todas as solicitações de pedido", description = "Retorna uma página com as solicitações de pedido cadastradas")
    @ApiResponse(responseCode = "200", description = "Página de solicitações retornada com sucesso")
    public ResponseEntity<Page<SolicitacaoDePedidoDTO>> getAllSolicitacoes(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(solicitacaoDePedidoService.getAllSolicitacoes(pageable));
    }

    /**
     * Busca uma solicitação de pedido específica por ID.
     *
     * <p>Retorna detalhes completos da solicitação incluindo todos os itens e cotações.</p>
     *
     * @param id Identificador único da solicitação
     * @return ResponseEntity contendo SolicitacaoDePedidoDTO completo
     * @throws jakarta.persistence.EntityNotFoundException se solicitação não encontrada
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar solicitação de pedido por ID", description = "Retorna uma solicitação de pedido específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> getSolicitacaoById(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoDePedidoService.getSolicitacaoById(id));
    }

    /**
     * Cria uma nova solicitação de pedido.
     *
     * <p>Valida e persiste nova solicitação com seus itens.
     * A solicitação é criada com status PENDENTE por padrão.</p>
     *
     * <p><b>Validações realizadas:</b></p>
     * <ul>
     *   <li>Itens obrigatórios (lista não vazia)</li>
     *   <li>Cada item deve ter nome e quantidade válida</li>
     *   <li>Observação com limite de caracteres</li>
     * </ul>
     *
     * @param solicitacaoDePedidoDTO Dados da solicitação a ser criada
     * @return ResponseEntity com SolicitacaoDePedidoDTO criada e status 201
     * @throws IllegalArgumentException se dados inválidos
     */
    @PostMapping
    @Operation(summary = "Criar nova solicitação de pedido", description = "Cria uma nova solicitação de pedido com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> createSolicitacao(@RequestBody @Valid SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacaoDePedidoService.createSolicitacao(solicitacaoDePedidoDTO));
    }

    /**
     * Atualiza uma solicitação de pedido existente.
     *
     * <p>Atualiza dados da solicitação identificada pelo ID.
     * Itens podem ser adicionados, removidos ou modificados.</p>
     *
     * @param id ID da solicitação a atualizar
     * @param solicitacaoDePedidoDTO Novos dados da solicitação
     * @return ResponseEntity com SolicitacaoDePedidoDTO atualizada
     * @throws jakarta.persistence.EntityNotFoundException se solicitação não encontrada
     * @throws IllegalArgumentException se dados inválidos
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar solicitação de pedido", description = "Atualiza os dados de uma solicitação de pedido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> updateSolicitacao(@PathVariable Long id, @RequestBody @Valid SolicitacaoDePedidoDTO solicitacaoDePedidoDTO) {
        return ResponseEntity.ok(solicitacaoDePedidoService.updateSolicitacao(id, solicitacaoDePedidoDTO));
    }

    /**
     * Remove uma solicitação de pedido do sistema.
     *
     * <p>Deleta permanentemente a solicitação e todos os seus itens.
     * Cotações associadas também são removidas (cascade).</p>
     *
     * <p><b>IMPORTANTE:</b> Esta operação não pode ser desfeita.
     * Considere arquivar em vez de deletar para manter histórico.</p>
     *
     * @param id ID da solicitação a ser removida
     * @return ResponseEntity com status 204 (NO_CONTENT)
     * @throws jakarta.persistence.EntityNotFoundException se solicitação não encontrada
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir solicitação de pedido", description = "Remove uma solicitação de pedido pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Solicitação excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada")
    })
    public ResponseEntity<Void> deleteSolicitacao(@PathVariable Long id) {
        solicitacaoDePedidoService.deleteSolicitacao(id);
        return ResponseEntity.noContent().build();
    }

    // ===== ENDPOINTS DE TRANSIÇÃO DE ESTADO =====

    /**
     * Envia pedido para aprovação (EM_NEGOCIACAO → PENDENTE_APROVACAO).
     * Finaliza a negociação e encaminha para o aprovador.
     */
    @PostMapping("/{id}/enviar-para-aprovacao")
    @Operation(
            summary = "Enviar pedido para aprovação",
            description = "Finaliza a negociação e envia o pedido para aprovação (EM_NEGOCIACAO → PENDENTE_APROVACAO)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido enviado para aprovação com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido para envio"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> enviarParaAprovacao(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid AcaoPedidoDTO dto
    ) {
        AcaoPedidoDTO dtoNonNull = dto != null ? dto : new AcaoPedidoDTO(null);
        return ResponseEntity.ok(solicitacaoDePedidoService.enviarParaAprovacao(id, dtoNonNull));
    }

    /**
     * Aprova um pedido (PENDENTE_APROVACAO → APROVADO).
     * Ação restrita ao aprovador.
     */
    @PostMapping("/{id}/aprovar")
    @Operation(
            summary = "Aprovar pedido",
            description = "Aprova o pedido permitindo prosseguir com a compra (PENDENTE_APROVACAO → APROVADO)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido aprovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido para aprovação"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> aprovarPedido(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid AcaoPedidoDTO dto
    ) {
        AcaoPedidoDTO dtoNonNull = dto != null ? dto : new AcaoPedidoDTO(null);
        return ResponseEntity.ok(solicitacaoDePedidoService.aprovarPedido(id, dtoNonNull));
    }

    /**
     * Cancela um pedido.
     * Pode ser feito em EM_NEGOCIACAO ou PENDENTE_APROVACAO.
     */
    @PostMapping("/{id}/cancelar")
    @Operation(
            summary = "Cancelar pedido",
            description = "Cancela o pedido (EM_NEGOCIACAO ou PENDENTE_APROVACAO → CANCELADO)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> cancelarPedido(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid AcaoPedidoDTO dto
    ) {
        AcaoPedidoDTO dtoNonNull = dto != null ? dto : new AcaoPedidoDTO(null);
        return ResponseEntity.ok(solicitacaoDePedidoService.cancelarPedido(id, dtoNonNull));
    }

    /**
     * Devolve pedido para edição (PENDENTE_APROVACAO → EM_NEGOCIACAO).
     * Aprovador devolve para o comprador fazer ajustes.
     * Requer motivo obrigatório.
     */
    @PostMapping("/{id}/devolver-para-edicao")
    @Operation(
            summary = "Devolver pedido para edição",
            description = "Devolve o pedido para o comprador fazer ajustes (PENDENTE_APROVACAO → EM_NEGOCIACAO). Requer motivo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido devolvido para edição com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido para devolução ou motivo inválido"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<SolicitacaoDePedidoDTO> devolverParaEdicao(
            @PathVariable Long id,
            @RequestBody @Valid DevolverPedidoDTO dto
    ) {
        return ResponseEntity.ok(solicitacaoDePedidoService.devolverParaEdicao(id, dto));
    }
}