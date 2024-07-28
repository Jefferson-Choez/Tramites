package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TramiteDTO {
    private Long id;
    private String departamento;
    private String descripcion;
    private List<EventoDTO> eventos;
}
