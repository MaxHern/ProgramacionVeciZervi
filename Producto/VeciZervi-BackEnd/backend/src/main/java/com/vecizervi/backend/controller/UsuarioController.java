package com.vecizervi.backend.controller;

import com.vecizervi.backend.model.Usuario;
import com.vecizervi.backend.repository.UsuarioRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/usuarios") 
public class UsuarioController {

    @Autowired 
    private UsuarioRepository usuarioRepository;

    // Scrum 1 y 2: Registro de Usuario y Contraseña
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        
        if (nuevoUsuario.getRut() == null || nuevoUsuario.getCorreo() == null) {
            return ResponseEntity.badRequest().body("Faltan datos obligatorios (RUT o Correo).");
        }
        if (nuevoUsuario.getContrasenaEnCriptada() == null || nuevoUsuario.getContrasenaEnCriptada().length() < 8) {
            return ResponseEntity.badRequest().body("La contraseña debe tener al menos 8 caracteres.");
        }
        // Guardar el usuario real en la base de datos de MySQL
        usuarioRepository.save(nuevoUsuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody Usuario loginRequest) {
        Usuario usuarioDB = usuarioRepository.findByCorreo(loginRequest.getCorreo());

        if (usuarioDB == null) {
            return ResponseEntity.status(401).body("Correo incorrecto.");
        }

        // 1. VERIFICAR SI ESTÁ BLOQUEADO
        if (usuarioDB.getCuentaBloqueadaHasta() != null) {
            if (LocalDateTime.now().isBefore(usuarioDB.getCuentaBloqueadaHasta())) {
                return ResponseEntity.status(403).body("Cuenta bloqueada por múltiples intentos fallidos. Intenta en 15 minutos.");
            } else {
                usuarioDB.setIntentosFallidos(0);
                usuarioDB.setCuentaBloqueadaHasta(null);
                usuarioRepository.save(usuarioDB);
            }
        }

        // 2. VERIFICAR CONTRASEÑA
        if (!usuarioDB.getContrasenaEnCriptada().equals(loginRequest.getContrasenaEnCriptada())) {
            // Contraseña mal: Aumentamos los errores
            usuarioDB.setIntentosFallidos(usuarioDB.getIntentosFallidos() + 1);
            
            // Si llega a 5, lo bloqueamos por 15 minutos
            if (usuarioDB.getIntentosFallidos() >= 5) {
                usuarioDB.setCuentaBloqueadaHasta(LocalDateTime.now().plusMinutes(15));
                usuarioRepository.save(usuarioDB);
                return ResponseEntity.status(403).body("Has fallado 5 veces. Cuenta bloqueada por 15 minutos.");
            }
            
            usuarioRepository.save(usuarioDB);
            return ResponseEntity.status(401).body("Contraseña incorrecta. Intento " + usuarioDB.getIntentosFallidos() + " de 5.");
        }

        usuarioDB.setIntentosFallidos(0);
        usuarioDB.setCuentaBloqueadaHasta(null);
        usuarioRepository.save(usuarioDB);

        return ResponseEntity.ok(usuarioDB);
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> actualizarPerfil(@PathVariable Long id, @RequestBody Usuario datosNuevos) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombres(datosNuevos.getNombres());
            usuario.setApellidos(datosNuevos.getApellidos());
            // No permitimos que cambien el RUT ni el Correo por seguridad
            usuarioRepository.save(usuario);
            return ResponseEntity.ok(usuario);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cambiar-clave")
    public ResponseEntity<?> cambiarClave(@PathVariable Long id, @RequestParam String claveAntigua, @RequestParam String claveNueva) {
        return usuarioRepository.findById(id).map(usuario -> {
            if (!usuario.getContrasenaEnCriptada().equals(claveAntigua)) {
                return ResponseEntity.badRequest().body("La clave antigua no coincide");
            }
            if (claveNueva.length() < 8) {
                return ResponseEntity.badRequest().body("La nueva clave debe tener al menos 8 caracteres");
            }
            usuario.setContrasenaEnCriptada(claveNueva);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Contraseña cambiada con éxito");
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/recuperar-clave")
    public ResponseEntity<?> recuperarClave(@RequestParam String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Si el correo existe, se ha enviado un link de recuperación."); // Mensaje ambiguo por seguridad (anti-hackers)
        }

        // Generamos un código temporal de 6 letras/números
        String tokenTemporal = java.util.UUID.randomUUID().toString().substring(0, 6);
        usuario.setTokenRecuperacion(tokenTemporal);
        usuarioRepository.save(usuario);
 
        System.out.println("SIMULACIÓN DE EMAIL -> Para: " + correo + " | Tu código de recuperación es: " + tokenTemporal);

        return ResponseEntity.ok("Si el correo existe, se ha enviado un código de recuperación.");
    }
}
