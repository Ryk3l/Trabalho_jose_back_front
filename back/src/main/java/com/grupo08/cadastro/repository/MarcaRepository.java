package com.grupo08.cadastro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo08.cadastro.entity.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    List<Marca> findTop10ByNomeContainingIgnoreCaseOrderByNomeAsc(String filtro);
}
