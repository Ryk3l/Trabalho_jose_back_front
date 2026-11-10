package com.grupo08.cadastro;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.grupo08.cadastro.controller.MarcaController;
import com.grupo08.cadastro.entity.Marca;
import com.grupo08.cadastro.repository.MarcaRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:marcas;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class MarcaIntegrationTests {

    @Autowired
    private MarcaController controller;

    @Autowired
    private MarcaRepository repository;

    private MockMvc mvc;

    @BeforeEach
    void configurarApi() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void cadastrarPersisteComIdGerado() throws Exception {
        mvc.perform(post("/api/marcas").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"Marca Exemplo","cnpj":"12.345.678/0001-95"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Marca Exemplo"))
                .andExpect(jsonPath("$.cnpj").value("12.345.678/0001-95"));

        assertThat(repository.findAll()).singleElement().satisfies(marca -> {
            assertThat(marca.getId()).isPositive();
            assertThat(marca.getNome()).isEqualTo("Marca Exemplo");
            assertThat(marca.getCnpj()).isEqualTo("12.345.678/0001-95");
        });
    }

    @Test
    void listarSemFiltroRetornaSomenteDezEmOrdemAlfabetica() throws Exception {
        for (int i = 12; i >= 1; i--) {
            repository.save(new Marca("Marca %02d".formatted(i), "%014d".formatted(i)));
        }

        mvc.perform(get("/api/marcas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].nome").value("Marca 01"))
                .andExpect(jsonPath("$[9].nome").value("Marca 10"));
    }

    @Test
    void listarFiltraSemDiferenciarMaiusculasELimitaDez() throws Exception {
        repository.save(new Marca("Outra empresa", "99999999999999"));
        for (int i = 12; i >= 1; i--) {
            repository.save(new Marca("Marca %02d".formatted(i), "%014d".formatted(i)));
        }

        mvc.perform(get("/api/marcas").param("filtro", "  mARcA  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].nome").value("Marca 01"))
                .andExpect(jsonPath("$[9].nome").value("Marca 10"));
    }

    @Test
    void filtroVazioListaTodasESemCorrespondenciaRetornaArrayVazio() throws Exception {
        repository.save(new Marca("Exemplo", "12345678000195"));
        mvc.perform(get("/api/marcas").param("filtro", "   "))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/marcas").param("filtro", "inexistente"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }
}
