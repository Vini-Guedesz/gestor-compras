package com.gestordecompras.gestorcomprasbackend.mapper;

import com.gestordecompras.gestorcomprasbackend.dto.cotacao.HistoricoCotacaoDTO;
import com.gestordecompras.gestorcomprasbackend.model.cotacao.HistoricoCotacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoricoCotacaoMapper {

    @Mapping(target = "temAnexoAnterior", expression = "java(historico.getAnexoPdfAnterior() != null)")
    @Mapping(target = "temAnexoNovo", expression = "java(historico.getAnexoPdfNovo() != null)")
    HistoricoCotacaoDTO toDTO(HistoricoCotacao historico);
}
