package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tramites")
public class TramiteController {

    private static final Logger logger = LoggerFactory.getLogger(TramiteController.class);

    private final TramiteService tramiteService;

    @Autowired
    public TramiteController(TramiteService tramiteService) {
        this.tramiteService = tramiteService;
    }

    @GetMapping("/tramites/nuevo")
    public String mostrarFormularioDeTramite(Model model) {
        model.addAttribute("tramite", new Tramite());
        return "Nuevo-tramite";
    }

    @PostMapping("/tramites/guardar")
    public String guardarTramite(@ModelAttribute Tramite tramite,
                                 @RequestParam("archivos") MultipartFile[] archivos,
                                 Principal principal) {
        try {
            tramiteService.guardarTramite(tramite, archivos, principal.getName());
        } catch (Exception e) {
            logger.error("Error al guardar el trámite", e);
            return "Nuevo-tramite";
        }
        return "redirect:/tramites/lista";
    }

    @GetMapping("/mis-tramites")
    public String listarMisTramites(Model model, Principal principal) {
        String emailUsuario = principal.getName();
        List<Tramite> tramites = tramiteService.obtenerTramitesPorUsuario(emailUsuario);
        model.addAttribute("tramites", tramites);
        return "Lista-tramites";
    }
}
