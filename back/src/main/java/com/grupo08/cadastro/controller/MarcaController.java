package com.grupo08.cadastro.controller;

import com.grupo08.cadastro.entity.Marca;
import com.grupo08.cadastro.service.MarcaService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    
    @PostMapping
    public ResponseEntity<Marca> inserir(@Valid @RequestBody Marca marca) {
        Marca marcaSalva = marcaService.salvar(marca);
        return ResponseEntity.status(HttpStatus.CREATED).body(marcaSalva);
    }


    @GetMapping
    public ResponseEntity<List<Marca>> listar(@RequestParam(required = false) String filtro) {
        List<Marca> marcas = marcaService.listar(filtro);
        return ResponseEntity.ok(marcas);
    }

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErroValidacao(MethodArgumentNotValidException e) {
        Map<String, String> erros = new HashMap<>();
        for (FieldError erro : e.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(erros);
    }

    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarCnpjDuplicado(DataIntegrityViolationException e) {
        Map<String, String> erro = new HashMap<>();
        erro.put("mensagem", "Ja existe uma marca cadastrada com esse CNPJ");
        return ResponseEntity.badRequest().body(erro);
    }
}
