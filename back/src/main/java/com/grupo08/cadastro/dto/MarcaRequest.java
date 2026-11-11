package com.grupo08.cadastro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dados aceitos pela API ao cadastrar uma marca. */
public record MarcaRequest(
        @NotBlank(message = "O nome é obrigatório.")
        // Limite provisório a alinhar com o tamanho definido na entidade Marca.
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotBlank(message = "O CNPJ é obrigatório.")
        // Permite CNPJ com ou sem máscara; validação dos dígitos fica pendente de alinhamento.
        @Size(max = 18, message = "O CNPJ deve ter no máximo 18 caracteres.")
        String cnpj
) {
}
