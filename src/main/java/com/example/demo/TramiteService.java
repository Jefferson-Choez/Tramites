package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class TramiteService {

    private final TramiteRepository tramiteRepository;
    private final ArchivoRepository archivoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public TramiteService(TramiteRepository tramiteRepository, ArchivoRepository archivoRepository,
                          UsuarioRepository usuarioRepository) {
        this.tramiteRepository = tramiteRepository;
        this.archivoRepository = archivoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void guardarTramite(Tramite tramite, MultipartFile[] archivos, String emailUsuario) throws IOException {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        tramite.setUsuario(usuario);
        tramite.setEstado("registrado");
        tramite.setFechaCreacion(LocalDate.now());

        tramiteRepository.save(tramite);

        for (MultipartFile archivo : archivos) {
            Archivo nuevoArchivo = new Archivo();
            nuevoArchivo.setNombre(archivo.getOriginalFilename());
            nuevoArchivo.setTipo(archivo.getContentType());
            nuevoArchivo.setDatos(archivo.getBytes());
            nuevoArchivo.setTramite(tramite);
            archivoRepository.save(nuevoArchivo);
        }
    }

    public List<Tramite> obtenerTramitesPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return tramiteRepository.findByUsuario(usuario);
    }
}
