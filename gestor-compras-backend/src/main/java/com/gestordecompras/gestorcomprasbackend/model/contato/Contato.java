package com.gestordecompras.gestorcomprasbackend.model.contato;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "contato")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "telefone_fixo", length = 20)
    private String telefoneFixo;

    @Column(name = "rotulo_telefone_fixo", length = 100)
    private String rotuloTelefoneFixo;

    @Column(name = "celular", length = 20)
    private String celular;

    @Column(name = "rotulo_celular", length = 100)
    private String rotuloCelular;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "rotulo_email", length = 100)
    private String rotuloEmail;

    @OneToMany(
        mappedBy = "contato",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("ordemExibicao ASC, id ASC")
    private List<ContatoAdicional> contatosAdicionais = new ArrayList<>();

    public void setContatosAdicionais(List<ContatoAdicional> contatosAdicionais) {
        this.contatosAdicionais.clear();
        if (contatosAdicionais != null) {
            contatosAdicionais.forEach(this::addContatoAdicional);
        }
    }

    public void addContatoAdicional(ContatoAdicional contatoAdicional) {
        if (contatoAdicional == null) return;
        contatoAdicional.setContato(this);
        this.contatosAdicionais.add(contatoAdicional);
    }

    public void removeContatoAdicional(ContatoAdicional contatoAdicional) {
        if (contatoAdicional == null) return;
        contatoAdicional.setContato(null);
        this.contatosAdicionais.remove(contatoAdicional);
    }
}
