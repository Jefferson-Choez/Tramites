package com.example.demo;

import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Getter
@Setter
@Controller
@Transactional
public class PasswordController {

    private final UserService userService;
    private final EmailService emailService;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public PasswordController(UserService userService, EmailService emailService, UsuarioRepository usuarioRepository,
                              BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.emailService = emailService;
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @GetMapping("/RecPass")
    public String showPassword(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "RecPass";
    }

    @PostMapping("/RecPass")
    public String forgotPassword(HttpServletRequest request,
            @RequestParam("email") String email,
            Model model) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(usuario, token);
            emailService.sendResetTokenEmail(getAppUrl(request), token, usuario);
            model.addAttribute("successMessage",
                    "Se ha enviado un enlace a su correo electrónico para recuperar la contraseña.");
            return "RecPass";
        } else {
            model.addAttribute("errorMessage", "No existe una cuenta con el correo electrónico proporcionado");
        }
            return "RecPass";
        }


    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @Transactional
    @GetMapping("/RestPass")
    public String showResetPasswordForm() {
        return "RestPass";
    }

    @Transactional
    @PostMapping("/RestPass")
    public String processResetPasswordForm(
                                           @RequestParam("token") String token,
                                           @RequestParam("newPassword") String newPassword,
                                           @RequestParam("confirmPassword") String confirmPassword,
                                           Model model, RedirectAttributes redirectAttributes
    ) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Las contraseñas no coinciden.");
            return "redirect:/RestPass?token=" + token;
        }

        String result = userService.validatePasswordResetToken(token);
        if (result != null) {
            model.addAttribute("message", "El enlace de restablecimiento ha caducado");
            return "RestPass";
        }

        Usuario usuario = userService.getUserByPasswordResetToken(token);
        if (usuario != null) {
            userService.changeUserPassword(usuario, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Tu contraseña ha sido restablecida.");
            return "redirect:Login";
        } else {
            model.addAttribute("message", "No se ha encontrado un usuario para este token.");
            return "RestPass";
        }
    }
}

