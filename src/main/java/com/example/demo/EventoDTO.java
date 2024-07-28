package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventoDTO {
    private Long id;
    private String tipo;
    private LocalDateTime fechaHora;
    private String usuario;
    private String departamento;
    private String comentario;
    private String descripcion;
}
