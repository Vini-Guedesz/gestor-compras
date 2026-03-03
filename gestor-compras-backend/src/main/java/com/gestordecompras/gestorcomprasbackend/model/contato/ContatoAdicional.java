package com.gestordecompras.gestorcomprasbackend.model.contato;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "contato_adicional")
public class ContatoAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome_contato", length = 100)
    private String nomeContato;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contato", nullable = false, length = 30)
    private TipoContatoAdicional tipoContato;

    @NotBlank
    @Column(name = "valor_contato", nullable = false, length = 255)
    private String valorContato;

    @Column(name = "ordem_exibicao")
    private Integer ordemExibicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contato_id", nullable = false)
    private Contato contato;
}

