package com.example.demo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "REPORTE", schema = "JCHOEZ")
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reporte_seq")
    @SequenceGenerator(name = "reporte_seq", sequenceName = "REPORTE_SEQ", allocationSize = 1)
    private Long id;

    private String tipoReporte;
    private String tipoUsuarioReporte;
    private String tipoTramiteReporte;
    private String estado;
    private LocalDate fechaInicioUsuarios;
    private LocalDate fechaFinUsuarios;
    private LocalDate fechaInicioTramites;
    private LocalDate fechaFinTramites;
    private String formatoReporte;

    @ManyToOne
    @JoinColumn(name = "tramite_id")
    private Tramite tramite;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Reporte() {}
}