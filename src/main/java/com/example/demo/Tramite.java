package com.example.demo;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Tramite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tipo;
    private String descripcion;
    private String estado;
    private LocalDate fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "usuario_ID")
    private Usuario usuario;

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL)
    private List<Archivo> archivos;

}

