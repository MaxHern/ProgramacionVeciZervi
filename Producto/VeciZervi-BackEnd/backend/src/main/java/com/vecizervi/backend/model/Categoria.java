package com.vecizervi.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    @Column(name = "nombre_categoria", nullable = false, length = 50)
    private String nombreCategoria;

    // Constructores vacíos y con parámetros (Obligatorio para Spring Boot)
    public Categoria() {}

    // ¡No olvides generar los Getters y Setters aquí abajo!
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
}