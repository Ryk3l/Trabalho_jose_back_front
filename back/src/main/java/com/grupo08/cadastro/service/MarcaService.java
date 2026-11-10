package com.grupo08.cadastro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.grupo08.cadastro.entity.Marca;
import com.grupo08.cadastro.repository.MarcaRepository;

@Service
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    public Marca salvar(Marca marca) {
        return marcaRepository.save(marca);
    }

    public List<Marca> listar(String filtro) {
        return marcaRepository.findTop10ByNomeContainingIgnoreCaseOrderByNomeAsc(filtro);
    }
}
