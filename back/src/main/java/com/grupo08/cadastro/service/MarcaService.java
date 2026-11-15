package com.grupo08.cadastro.service;

import com.grupo08.cadastro.dto.MarcaRequest;
import com.grupo08.cadastro.dto.MarcaResponse;
import com.grupo08.cadastro.entity.Marca;
import com.grupo08.cadastro.repository.MarcaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    /** Limpa os campos recebidos, salva a entidade e devolve o DTO da API. */
    @Transactional
    public MarcaResponse salvar(MarcaRequest request) {
        Marca marca = new Marca();
        // Remove espaços acidentais no começo e no fim antes de persistir.
        marca.setNome(request.nome().trim());
        marca.setCnpj(request.cnpj().trim());

        return paraResponse(marcaRepository.save(marca));
    }

    /**
     * Devolve até 10 marcas em ordem alfabética. O filtro vazio inclui todos
     * os nomes; a consulta do repository ignora diferenças entre maiúsculas
     * e minúsculas.
     */
    public List<MarcaResponse> listar(String filtro) {
        // A consulta continua limitada a 10 mesmo quando não há filtro.
        String filtroNormalizado = filtro == null ? "" : filtro.trim();

        return marcaRepository
                // O método do repository aplica filtro, limite e ordenação no banco.
                .findTop10ByNomeContainingIgnoreCaseOrderByNomeAsc(filtroNormalizado)
                .stream()
                .map(this::paraResponse)
                .toList();
    }

    /** Converte a entidade persistida para o formato exposto pela API. */
    private MarcaResponse paraResponse(Marca marca) {
        return new MarcaResponse(marca.getId(), marca.getNome(), marca.getCnpj());
    }
}
