package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AuthController {

    @GetMapping("/Login")
    public String Login() {//muestra la pagina de inicio de sesion
        return "Index";
    }

    @GetMapping("/Panel-usuario")
    public String panelUsuario(Model model, Principal principal) {//utiliza el objeto principal para obtener
        model.addAttribute("username", principal.getName());// el nombre del usuario autenticado
        return "Panel-usuario";
    }

    @GetMapping("/logout")
    public String logoutPage() {//envia al usuario al inicio de sesion
        return "redirect:/Login?logout";
    }

}

