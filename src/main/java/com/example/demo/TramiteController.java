package com.example.demo;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class TramiteController {

    private static final Logger logger = LoggerFactory.getLogger(TramiteController.class);

    private final TramiteService tramiteService;
    private final ComentarioService comentarioService;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public TramiteController(TramiteService tramiteService,
                             ComentarioService comentarioService,
                             UserService userService, EmailService emailService) {
        this.tramiteService = tramiteService;
        this.comentarioService = comentarioService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/NuevoTramite")
    public String mostrarFormularioDeTramite(Model model, Principal principal) {
        model.addAttribute("tramite", new Tramite());
        model.addAttribute("username", principal.getName());
        return "NuevoTramite";
    }

    @PostMapping("/NuevoTramite")
    public String guardarTramite(@RequestParam("tipoTramite") String tipoTramite,
                                 @RequestParam("subtipoTramite") String subtipoTramite,
                                 @RequestParam("descripcion") String descripcion,
                                 Principal principal) {
        String emailUsuario = principal.getName();
        Tramite tramite = new Tramite();
        tramite.setTipo(tipoTramite);
        tramite.setSubtipo(subtipoTramite);
        tramite.setDescripcion(descripcion);
        try {
            tramiteService.guardarTramite(tramite, emailUsuario);
            emailService.sendEmail(emailUsuario, "Trámite Guardado", "Tu trámite ha sido registrado exitosamente.");
        } catch (Exception e) {
            logger.error("Error al guardar el trámite", e);
            return "redirect:/NuevoTramite?error";
        }
        return "redirect:/ListaTramites";
    }

    @GetMapping("/ListaTramites")
    public String listarMisTramites(Model model, Principal principal) {
        String emailUsuario = principal.getName();
        List<Tramite> tramites = tramiteService.obtenerTramitesPorUsuario(emailUsuario);
        model.addAttribute("tramites", tramites);
        model.addAttribute("username", emailUsuario);
        return "ListaTramites";
    }

    @PostMapping("/eliminarTramite")
    public String eliminarTramite(@RequestParam("id") Long id, Principal principal) throws MessagingException {
        String emailUsuario = principal.getName();
        tramiteService.eliminarTramite(id);
        emailService.sendEmail(emailUsuario, "Trámite Eliminado", "Tu trámite ha sido eliminado.");
        return "redirect:/ListaTramites";
    }

    @GetMapping("/GestionarTramitesAdmin1")
    public String gestionarTramitesAdmin1(@RequestParam(value = "page", defaultValue = "0") int page,
                                          Model model, Principal principal) {
        String username = principal.getName();
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        String departamento = userDetails.getDepartamento();

        Page<Tramite> tramites = tramiteService.findByDepartamento(departamento, PageRequest.of(page, 10, Sort.by("fechaCreacion").descending()));

        model.addAttribute("tramites", tramites);
        model.addAttribute("username", username);
        return "GestionarTramitesAdmin1";
    }

    @GetMapping("/GestionarTramitesAdmin2")
    public String gestionarTramites(@RequestParam(value = "filtro", required = false) String filtro,
                                    @RequestParam(value = "valor", required = false) String valor,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    Model model, Principal principal) {
        Page<Tramite> tramites;
        if ("estado".equals(filtro)) {
            tramites = tramiteService.findByEstado(valor, PageRequest.of(page, 10, Sort.by("fechaCreacion").descending()));
        } else if ("departamento".equals(filtro)) {
            tramites = tramiteService.findByDepartamento(valor, PageRequest.of(page, 10, Sort.by("fechaCreacion").descending()));
        } else if ("cedula".equals(filtro)) {
            tramites = tramiteService.findByUsuarioCedula(valor, PageRequest.of(page, 10, Sort.by("fechaCreacion").descending()));
        } else {
            tramites = tramiteService.findAll(PageRequest.of(page, 10, Sort.by("fechaCreacion").descending()));
        }

        model.addAttribute("tramites", tramites);
        model.addAttribute("filtro", filtro);
        model.addAttribute("valor", valor);
        model.addAttribute("username", principal.getName());
        return "GestionarTramitesAdmin2";
    }

    @GetMapping("/DetalleTramite/{id}")
    public String verDetalleTramite(@PathVariable("id") Long id, Model model, Principal principal) {
        Tramite tramite = tramiteService.obtenerTramitePorId(id);
        List<Comentario> comentarios = comentarioService.obtenerComentariosPorTramiteId(id);
        model.addAttribute("tramite", tramite);
        model.addAttribute("comentarios", comentarios);
        model.addAttribute("username", principal.getName());
        return "DetalleTramite";
    }

    @PostMapping("/AgregarComentario")
    public String agregarComentario(@RequestParam("idTramite") Long idTramite,
                                    @RequestParam("comentario") String comentario,
                                    Principal principal) {
        String emailUsuario = principal.getName();
        Usuario usuario = userService.findByEmail(emailUsuario);
        comentarioService.agregarComentario(idTramite, comentario, usuario);
        return "redirect:/DetalleTramite/" + idTramite;
    }


    @PostMapping("/AsignarArea")
    public String asignarArea(@RequestParam("idTramite") Long idTramite,
                              @RequestParam("area") String area,
                              Principal principal) {
        String emailUsuario = principal.getName();
        Usuario usuario = userService.findByEmail(emailUsuario);
        tramiteService.asignarArea(idTramite, area, usuario);
        return "redirect:/DetalleTramite/" + idTramite;
    }

    @PostMapping("/CambiarEstado")
    public String cambiarEstado(@RequestParam("idTramite") Long idTramite,
                                @RequestParam("estado") String estado,
                                Principal principal) throws MessagingException {
        String emailUsuario = principal.getName();
        Usuario usuario = userService.findByEmail(emailUsuario);
        tramiteService.cambiarEstado(idTramite, estado, usuario);
        emailService.sendEmail(emailUsuario, "Estado de Trámite Cambiado",
                "El estado de tu trámite ha sido cambiado a " + estado + ". " +
                        "Revisa los detalles");
        return "redirect:/DetalleTramite/" + idTramite;
    }

    @GetMapping("/DetalleEventos/{id}")
    public String detalleEventos(@PathVariable Long id, Model model, Principal principal) {
        Tramite tramite = tramiteService.obtenerTramitePorId(id);
        TramiteDTO tramiteDTO = tramiteService.convertToTramiteDTO(tramite);
        model.addAttribute("tramite", tramiteDTO);
        model.addAttribute("username", principal.getName());
        return "DetalleEventos";
    }

}