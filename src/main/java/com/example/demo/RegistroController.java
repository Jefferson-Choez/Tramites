package com.example.demo;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import java.util.Locale;

@Validated
@Controller
@RequestMapping("/Registro")
public class RegistroController {

    private final Logger logger = LoggerFactory.getLogger(RegistroController.class);
    private final UserService userService;
    private final EmailService emailService;
    private final MessageSource messageSource;

    @Autowired
    public RegistroController(UserService userService, EmailService emailService, MessageSource messageSource) {
        this.userService = userService;
        this.emailService = emailService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "Registro";
    }

    @PostMapping
    public String registrarUsuario(@Validated(Usuario.Registro.class)
                                   @ModelAttribute("usuario") Usuario usuario,
                                   BindingResult bindingResult, Model model) throws MessagingException {
        if (bindingResult.hasErrors()) {
            logger.error("Errores de validación presentes.");
            bindingResult.getAllErrors().forEach(error -> logger.error("{}: {}", error.getObjectName(), error.getDefaultMessage()));
            return "Registro";
        }

        if (!usuario.getPassword().equals(usuario.getConfirmPass())) {
            logger.error("Las contraseñas no coinciden.");
            String errorMessage = messageSource.getMessage("registration.error.password.mismatch", null, Locale.getDefault());
            model.addAttribute("error", errorMessage);
            return "Registro";
        }
        try {
            String resultado = userService.registerUser(usuario);
            String successMessage = messageSource.getMessage("registration.success", null, Locale.getDefault());
            if (!resultado.equals(successMessage)) {
                model.addAttribute("error", resultado);
                return "Registro";
            }
        } catch (Exception e) {
            String errorMessage = messageSource.getMessage("registration.error.generic", new Object[]{e.getMessage()}, Locale.getDefault());
            model.addAttribute("error", errorMessage);
            return "Registro";
        }

        emailService.sendRegistroNotificationEmail(usuario);
        return "redirect:/Registro/Registro-exitoso";
    }

    @GetMapping("/Registro-exitoso")
    public String registroExitoso() {
        return "Registro-exitoso";
    }

    @GetMapping("/Politica-privacidad")
    public String mostrarPoliticaPrivacidad(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "Politica-privacidad";
    }
}
