package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/Panel-gestion")
    public String panelGestion(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "Panel-gestion";
    }

    @GetMapping("/Panel-admin")
    public String panelAdmin(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "Panel-admin";
    }

    @GetMapping("/Roles")
    public String mostrarFormularioRoles(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "Roles";
    }

    @GetMapping("/buscarUsuario")
    public String buscarUsuario(@RequestParam("search") String search, Model model, Principal principal) {
        Usuario usuario = userService.findByEmailOrCedula(search);
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        } else {
            model.addAttribute("error", "Usuario no encontrado.");
        }
        model.addAttribute("username", principal.getName());
        return "Roles";
    }

    @PostMapping("/asignarRol")
    public String asignarRol(@RequestParam("usuarioId") Long usuarioId,
                             @RequestParam("rol") String rol,
                             @RequestParam("departamento") String departamento,
                             Model model, Principal principal) {
        try {
            userService.assignRoleToUser(usuarioId, rol, departamento);
            model.addAttribute("success", "Rol asignado exitosamente.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        Usuario usuario = userService.findById(usuarioId);
        model.addAttribute("usuario", usuario);
        model.addAttribute("username", principal.getName());
        return "Roles";
    }


}
