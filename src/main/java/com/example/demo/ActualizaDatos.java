package com.example.demo;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Locale;

@Controller
@Transactional
public class ActualizaDatos {

    private final UserService userService;
    private final MessageSource messageSource;

    @Autowired
    public ActualizaDatos(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping("/Actualizar-datos")
    public String mostrarFormulario(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            String email = ((CustomUserDetails) authentication.getPrincipal()).getEmail();
            Usuario usuario = userService.findByEmail(email);

            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                model.addAttribute("username", email);
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
                model.addAttribute("username", email);
            }

            userService.actualizarUsuario(usuario);
            String successMessage = messageSource.getMessage("update.user.success", null, Locale.getDefault());
            model.addAttribute("mensaje", successMessage);
        } catch (Exception e) {
            String errorMessage = messageSource.getMessage("update.user.error", new Object[]{e.getMessage()}, Locale.getDefault());
            model.addAttribute("error", errorMessage);
        }
        return "Actualizar-datos";
    }
}
