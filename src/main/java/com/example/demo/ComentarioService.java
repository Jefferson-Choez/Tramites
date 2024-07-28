package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final TramiteService tramiteService;

    @Autowired
    public ComentarioService(ComentarioRepository comentarioRepository, TramiteService tramiteService) {
        this.comentarioRepository = comentarioRepository;
        this.tramiteService = tramiteService;
    }

    public List<Comentario> obtenerComentariosPorTramiteId(Long tramiteId) {
        return comentarioRepository.findByTramiteId(tramiteId);
    }

    public void agregarComentario(Long tramiteId, String texto, Usuario usuario) {
        Comentario comentario = new Comentario();
        Tramite tramite = tramiteService.obtenerTramitePorId(tramiteId);
        tramite.setId(tramiteId);
        comentario.setTramite(tramite);
        comentario.setTexto(texto);
        comentario.setAutor(usuario.getPrimerNombre() + " " + usuario.getPrimerApellido());
        comentario.setFechaCreacion(LocalDateTime.now());
        comentario.setUsuario(usuario);
        comentarioRepository.save(comentario);

        tramiteService.registrarEvento(tramite, usuario, "Nuevo comentario", texto);

    }
}