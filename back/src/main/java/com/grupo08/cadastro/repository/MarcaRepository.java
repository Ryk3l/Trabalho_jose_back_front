package com.grupo08.cadastro.repository;

import com.grupo08.cadastro.entity.Marca;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    List<Marca> findTop10ByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
}
