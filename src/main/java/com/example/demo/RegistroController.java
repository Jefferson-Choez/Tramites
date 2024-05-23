package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Validated
@Controller
@RequestMapping("/Registro")
public class RegistroController {

    private final Logger logger = LoggerFactory.getLogger(RegistroController.class);
    private final UserService userService;

    @Autowired
    public RegistroController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "Registro";
    }

    @PostMapping
    public String registrarUsuario(@Validated(Usuario.Registro.class) @ModelAttribute("usuario") Usuario usuario,
                                   BindingResult bindingResult, Model model) {
        logger.debug("Intento de registro de usuario: {}", usuario);
        if (bindingResult.hasErrors()) {
            logger.error("Errores de validación presentes.");
            bindingResult.getAllErrors().forEach(error -> logger.error("{}: {}", error.getObjectName(), error.getDefaultMessage()));
            return "Registro";
        }

        if (!usuario.getPassword().equals(usuario.getConfirmPass())) {
            logger.error("Las contraseñas no coinciden.");
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "Registro";
        }

        String resultado = userService.registerUser(usuario);
        if (!resultado.equals("registro exitoso")) {
            model.addAttribute("error", resultado);
            return "Registro";
        }
            return "redirect:/Registro-exitoso";
        }

    @GetMapping("/Registro-exitoso")
    public String registroExitoso() {
        return "Registro-exitoso";
    }
}
