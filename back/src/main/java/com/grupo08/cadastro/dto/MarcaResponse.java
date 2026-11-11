package com.grupo08.cadastro.dto;

/** Dados devolvidos pela API; os nomes correspondem ao contrato do frontend. */
public record MarcaResponse(Long id, String nome, String cnpj) {
}
