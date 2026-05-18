package com.vecizervi.backend.controller;

import com.vecizervi.backend.model.Resena;
import com.vecizervi.backend.service.ResenaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @PostMapping
    public ResponseEntity<Resena> crearResena(@RequestBody Resena resena) {
        Resena nueva = resenaService.save(resena);
        return ResponseEntity.ok(nueva);
    }

    @GetMapping
    public List<Resena> listarResenas() {
        return resenaService.findAll();
    }
}
