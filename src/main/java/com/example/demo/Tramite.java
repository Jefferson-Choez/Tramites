package com.example.demo;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TRAMITE", schema = "JCHOEZ")
public class Tramite {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tramite_seq")
    @SequenceGenerator(name = "tramite_seq", sequenceName = "TRAMITE_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "SUBTIPO")
    private String subtipo;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @Column(name = "FECHA_CREACION")
    private LocalDate fechaCreacion;

    @Column(name = "FECHA_FINALIZACION")
    private LocalDate fechaFinalizacion;

    @Column(name = "DURACION")
    private Double duracion;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "DEPARTAMENTO")
    private String departamento;

    @Column(name = "COMENTARIO")
    private String comentario;

    @Column(name = "FECHA_COMENTARIO")
    private LocalDateTime fechaComentario;

    @Column(name = "USUARIO_COMENTARIO")
    private LocalDateTime UsuarioComentario;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "tramite", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Evento> eventos = new ArrayList<>();

    public Tramite() {}

}


