package com.vecizervi.backend.controller;

import com.vecizervi.backend.model.Categoria;
import com.vecizervi.backend.model.EstadoTrabajo;
import com.vecizervi.backend.model.Trabajo;
import com.vecizervi.backend.model.Usuario;
import com.vecizervi.backend.repository.CategoriaRepository;
import com.vecizervi.backend.repository.TrabajoRepository;
import com.vecizervi.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trabajos")
public class TrabajoController {

    @Autowired
    private TrabajoRepository trabajoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @PostMapping("/publicar")
    public ResponseEntity<?> publicarTrabajo(@RequestBody Trabajo nuevoTrabajo,
                                             @RequestParam Long idCliente,
                                             @RequestParam Long idCategoria) {

        if (nuevoTrabajo.getPrecio() == null || nuevoTrabajo.getPrecio() <= 0) {
            return ResponseEntity.badRequest().body("El precio debe ser un número mayor a 0.");
        }
        if (nuevoTrabajo.getTitulo() == null || nuevoTrabajo.getTitulo().length() < 5) {
            return ResponseEntity.badRequest().body("El título debe ser más descriptivo.");
        }

        Usuario cliente = usuarioRepository.findById(idCliente).orElse(null);
        Categoria categoria = categoriaRepository.findById(idCategoria).orElse(null);

        if (cliente == null) return ResponseEntity.badRequest().body("Cliente no encontrado.");
        if (categoria == null) return ResponseEntity.badRequest().body("Categoría no encontrada.");

        nuevoTrabajo.setCliente(cliente);
        nuevoTrabajo.setCategoria(categoria);
        nuevoTrabajo.setEstado(EstadoTrabajo.Disponible);

        trabajoRepository.save(nuevoTrabajo);
        return ResponseEntity.ok(nuevoTrabajo);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<Trabajo>> listarTrabajosDisponibles() {
        List<Trabajo> disponibles = trabajoRepository.findByEstado(EstadoTrabajo.Disponible);
        return ResponseEntity.ok(disponibles);
    }

    @PutMapping("/{id}/asignar")
    public ResponseEntity<?> marcarComoAsignado(@PathVariable Long id) {
        Optional<Trabajo> trabajoOpt = trabajoRepository.findById(id);
        if (trabajoOpt.isPresent()) {
            Trabajo trabajo = trabajoOpt.get();
            trabajo.setEstado(EstadoTrabajo.Asignado);
            trabajoRepository.save(trabajo);
            return ResponseEntity.ok("El trabajo ha sido marcado como Asignado.");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> marcarComoFinalizado(@PathVariable Long id) {
        Optional<Trabajo> trabajoOpt = trabajoRepository.findById(id);
        if (trabajoOpt.isPresent()) {
            Trabajo trabajo = trabajoOpt.get();
            trabajo.setEstado(EstadoTrabajo.Finalizado);
            trabajo.setFechaFinalizacion(LocalDateTime.now());
            trabajoRepository.save(trabajo);
            return ResponseEntity.ok("Trabajo finalizado con éxito a las " + trabajo.getFechaFinalizacion());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<Trabajo> getTodosLosTrabajos() {
        return trabajoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trabajo> getTrabajoPorId(@PathVariable Long id) {
        return trabajoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}