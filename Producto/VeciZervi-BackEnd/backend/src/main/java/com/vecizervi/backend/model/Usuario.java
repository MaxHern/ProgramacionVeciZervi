package com.vecizervi.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") 
    private Long id;

    @Column(unique = true, nullable = false)
    private String rut;

    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(name = "password", nullable = false)
    private String contrasenaEnCriptada; 

    private Integer intentosFallidos = 0; 
    private LocalDateTime cuentaBloqueadaHasta; 
    private String tokenRecuperacion;
}
