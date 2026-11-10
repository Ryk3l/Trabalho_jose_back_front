package com.grupo08.cadastro.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.grupo08.cadastro.entity.Marca;
import com.grupo08.cadastro.service.MarcaService;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Marca salvar(@RequestBody Marca marca) {
        return marcaService.salvar(marca);
    }

    @GetMapping
    public List<Marca> listar(@RequestParam(name = "filtro", required = false) String filtro) {
        return marcaService.listar(filtro);
    }
}
