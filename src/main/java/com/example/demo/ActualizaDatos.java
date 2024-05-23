package com.example.demo;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@Transactional
public class ActualizaDatos {

    private final UserService userService;

    @Autowired
    public ActualizaDatos(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/Actualizar-datos")
    public String mostrarFormulario(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            String email = ((CustomUserDetails) authentication.getPrincipal()).getEmail();
            Usuario usuario = userService.obtenerUsuarioPorEmail(email);

            if (usuario != null) {
                model.addAttribute("usuario", usuario);
            }
        }
        return "Actualizar-datos";
    }

    @PostMapping("/Actualizar-datos")
    public String procesarFormulario(Usuario usuario, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                String email = ((CustomUserDetails) authentication.getPrincipal()).getEmail();
                usuario.setEmail(email);
            }

            userService.actualizarUsuario(usuario);
            model.addAttribute("mensaje", "Usuario actualizado exitosamente.");
        } catch (Exception e) {
            model.addAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
        }
        return "Actualizar-datos";
    }
}
