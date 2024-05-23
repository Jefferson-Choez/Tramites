package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.security.Principal;

@Controller
public class AuthController {

    @GetMapping("/Login")
    public String Login() {
        return "Index";
    }

    @GetMapping("/Panel-usuario")
    public String panelUsuario(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "Panel-usuario";
    }

}




