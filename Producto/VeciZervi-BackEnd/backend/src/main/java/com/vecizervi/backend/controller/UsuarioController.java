package com.vecizervi.backend.controller;

import com.vecizervi.backend.model.Usuario;
import com.vecizervi.backend.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/usuarios") 
public class UsuarioController {

    @Autowired 
    private UsuarioRepository usuarioRepository;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        if (nuevoUsuario.getRut() == null || nuevoUsuario.getCorreo() == null)
            return ResponseEntity.badRequest().body("Faltan datos obligatorios (RUT o Correo).");
        if (nuevoUsuario.getContrasenaEnCriptada() == null || nuevoUsuario.getContrasenaEnCriptada().length() < 8)
            return ResponseEntity.badRequest().body("La contraseña debe tener al menos 8 caracteres.");
        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody Usuario loginRequest) {
        Usuario usuarioDB = usuarioRepository.findByCorreo(loginRequest.getCorreo());
        if (usuarioDB == null)
            return ResponseEntity.status(401).body("Correo incorrecto.");

        if (usuarioDB.getCuentaBloqueadaHasta() != null) {
            if (LocalDateTime.now().isBefore(usuarioDB.getCuentaBloqueadaHasta()))
                return ResponseEntity.status(403).body("Cuenta bloqueada. Intenta en 15 minutos.");
            else {
                usuarioDB.setIntentosFallidos(0);
                usuarioDB.setCuentaBloqueadaHasta(null);
                usuarioRepository.save(usuarioDB);
            }
        }

        if (!usuarioDB.getContrasenaEnCriptada().equals(loginRequest.getContrasenaEnCriptada())) {
            usuarioDB.setIntentosFallidos(usuarioDB.getIntentosFallidos() + 1);
            if (usuarioDB.getIntentosFallidos() >= 5) {
                usuarioDB.setCuentaBloqueadaHasta(LocalDateTime.now().plusMinutes(15));
                usuarioRepository.save(usuarioDB);
                return ResponseEntity.status(403).body("Cuenta bloqueada por 15 minutos.");
            }
            usuarioRepository.save(usuarioDB);
            return ResponseEntity.status(401).body("Contraseña incorrecta. Intento " + usuarioDB.getIntentosFallidos() + " de 5.");
        }

        usuarioDB.setIntentosFallidos(0);
        usuarioDB.setCuentaBloqueadaHasta(null);
        usuarioRepository.save(usuarioDB);
        return ResponseEntity.ok(usuarioDB);
    }

    // ← UN SOLO @GetMapping sin ruta
    @GetMapping
    public List<Usuario> getTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id, @RequestBody Usuario datosNuevos) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombres(datosNuevos.getNombres());
            usuario.setApellidos(datosNuevos.getApellidos());
            usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuario);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cambiar-clave")
    public ResponseEntity<?> cambiarClave(@PathVariable Long id,
                                          @RequestParam String claveAntigua,
                                          @RequestParam String claveNueva) {
        return usuarioRepository.findById(id).map(usuario -> {
            if (!usuario.getContrasenaEnCriptada().equals(claveAntigua))
                return ResponseEntity.badRequest().body("La clave antigua no coincide");
            if (claveNueva.length() < 8)
                return ResponseEntity.badRequest().body("La nueva clave debe tener al menos 8 caracteres");
            usuario.setContrasenaEnCriptada(claveNueva);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Contraseña cambiada con éxito");
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id))
            return ResponseEntity.notFound().build();
        usuarioRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado.");
    }

    @PostMapping("/recuperar-clave")
    public ResponseEntity<?> recuperarClave(@RequestParam String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null)
            return ResponseEntity.status(404).body("No existe una cuenta con ese correo.");

        String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);
        usuario.setTokenRecuperacion(codigo);
        usuarioRepository.save(usuario);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Código generado correctamente");
        respuesta.put("codigo", codigo);
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/verificar-token")
    public ResponseEntity<?> verificarToken(@RequestParam String correo,
                                            @RequestParam String token) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null || usuario.getTokenRecuperacion() == null)
            return ResponseEntity.status(400).body("Token inválido.");
        if (!usuario.getTokenRecuperacion().equals(token))
            return ResponseEntity.status(400).body("Código incorrecto.");
        return ResponseEntity.ok("Token válido.");
    }

    @PostMapping("/nueva-clave")
    public ResponseEntity<?> nuevaClave(@RequestParam String correo,
                                        @RequestParam String token,
                                        @RequestParam String nuevaClave) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null || usuario.getTokenRecuperacion() == null)
            return ResponseEntity.status(400).body("Token inválido.");
        if (!usuario.getTokenRecuperacion().equals(token))
            return ResponseEntity.status(400).body("Código incorrecto.");
        if (nuevaClave.length() < 8)
            return ResponseEntity.badRequest().body("Mínimo 8 caracteres.");

        usuario.setContrasenaEnCriptada(nuevaClave);
        usuario.setTokenRecuperacion(null);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }
}