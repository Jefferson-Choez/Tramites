package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TramiteService {

    private final TramiteRepository tramiteRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public TramiteService(TramiteRepository tramiteRepository, UsuarioRepository usuarioRepository) {
        this.tramiteRepository = tramiteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void guardarTramite(Tramite tramite, String username) throws Exception {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            tramite.setUsuario(usuario);
            tramite.setFechaCreacion(LocalDate.now());
            tramite.setEstado("Registrado");
            tramiteRepository.save(tramite);

            registrarEvento(tramite, usuario, "Registro del trámite", tramite.getDescripcion());
        } else {
            throw new Exception("Usuario no encontrado");
        }
    }

    public List<Tramite> obtenerTramitesPorUsuario(String email) {
        return tramiteRepository.findByUsuarioEmail(email);
    }

    public void eliminarTramite(Long id) {
        tramiteRepository.deleteById(id);
    }

    public Page<Tramite> findByEstado(String estado, Pageable pageable) {
        return tramiteRepository.findByEstado(estado, pageable);
    }

    public Page<Tramite> findByDepartamento(String departamento, Pageable pageable) {
        return tramiteRepository.findByDepartamento(departamento, pageable);
    }

    public Page<Tramite> findByUsuarioCedula(String cedula, Pageable pageable) {
        return tramiteRepository.findByUsuarioCedula(cedula, pageable);
    }

    public Page<Tramite> findAll(Pageable pageable) {
        return tramiteRepository.findAll(pageable);
    }

    public Tramite obtenerTramitePorId(Long id) {
        return tramiteRepository.findByIdWithEventos(id).orElseThrow(() -> new RuntimeException("Trámite no encontrado"));
    }

    @Transactional
    public void registrarEvento(Tramite tramite, Usuario usuario, String tipo, String descripcion) {
        Evento evento = new Evento();
        evento.setTramite(tramite);
        evento.setUsuario(usuario);
        evento.setTipo(tipo);
        evento.setDescripcion(descripcion);
        evento.setFechaHora(LocalDateTime.now());
        tramite.getEventos().add(evento);
        tramiteRepository.save(tramite);
    }

    @Transactional
    public void asignarArea(Long idTramite, String area, Usuario usuario) {
        Tramite tramite = obtenerTramitePorId(idTramite);
        if (tramite != null) {
            tramite.setDepartamento(area);
            tramiteRepository.save(tramite);

            registrarEvento(tramite, usuario, "Asignación de área", area);
        }
    }

    @Transactional
    public void cambiarEstado(Long idTramite, String estado, Usuario usuario) {
        Tramite tramite = obtenerTramitePorId(idTramite);
        if (tramite != null) {
            tramite.setEstado(estado);
            tramiteRepository.save(tramite);
            registrarEvento(tramite, usuario, "Cambio de estado", estado);
        }
    }

    @Transactional
    public TramiteDTO convertToTramiteDTO(Tramite tramite) {//Transferir datos entre las capas de la aplicacion
        TramiteDTO tramiteDTO = new TramiteDTO();
        tramiteDTO.setId(tramite.getId());
        tramiteDTO.setDescripcion(tramite.getDescripcion());
        tramiteDTO.setDepartamento(tramite.getDepartamento());

        List<EventoDTO> eventoDTOs = tramite.getEventos().stream()
                .sorted(Comparator.comparingLong(Evento::getId))
                .map(evento -> {
                    EventoDTO eventoDTO = new EventoDTO();
                    eventoDTO.setId(evento.getId());
                    eventoDTO.setTipo(evento.getTipo());
                    eventoDTO.setFechaHora(evento.getFechaHora());
                    eventoDTO.setUsuario(evento.getUsuario().getPrimerNombre()+" "+evento.getUsuario().getPrimerApellido());
                    eventoDTO.setDepartamento(evento.getUsuario().getDepartamento());
                    eventoDTO.setDescripcion(evento.getDescripcion());
                    return eventoDTO;
                })
                .collect(Collectors.toList());

        tramiteDTO.setEventos(eventoDTOs);
        return tramiteDTO;
    }

}
