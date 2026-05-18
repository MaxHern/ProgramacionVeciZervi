package com.vecizervi.backend.repository;
 
import com.vecizervi.backend.model.EstadoTrabajo;
import com.vecizervi.backend.model.Trabajo;
import com.vecizervi.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface TrabajoRepository extends JpaRepository<Trabajo, Long> {
    List<Trabajo> findByEstado(EstadoTrabajo estado);
    // FIX: método necesario para GET /trabajos/cliente/{id}
    List<Trabajo> findByCliente(Usuario cliente);
}