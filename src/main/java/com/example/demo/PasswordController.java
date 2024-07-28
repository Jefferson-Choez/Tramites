package com.example.demo;

import java.util.Optional;
import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;

@Getter
@Setter
@Controller
@Transactional
public class PasswordController {

    private final UserService userService;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final MessageSource messageSource;


    public PasswordController(UserService userService, UsuarioRepository usuarioRepository,
                              BCryptPasswordEncoder bCryptPasswordEncoder, EmailService emailService, MessageSource messageSource) {
        this.userService = userService;
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
        this.messageSource = messageSource;
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
            try {
                emailService.sendResetTokenEmail(getAppUrl(request), token, usuario);
                model.addAttribute("successMessage", messageSource.getMessage("password.reset.email.success", null, Locale.getDefault()));
            } catch (MessagingException e) {
                model.addAttribute("errorMessage", messageSource.getMessage("password.reset.email.error", null, Locale.getDefault()));
            }
        } else {
            model.addAttribute("errorMessage", messageSource.getMessage("password.reset.email.notfound", null, Locale.getDefault()));
        }
        return "RecPass";
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }


    @GetMapping("/RestPass")
    public String showResetPasswordForm() {
        return "RestPass";
    }


    @PostMapping("/RestPass")
    public String processResetPasswordForm(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model, RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", messageSource.getMessage("password.reset.error.password.mismatch", null, Locale.getDefault()));
            return "redirect:/RestPass?token=" + token;
        }

        String result = userService.validatePasswordResetToken(token);
        if (result != null) {
            model.addAttribute("message", messageSource.getMessage("password.reset.token.expired", null, Locale.getDefault()));
            return "RestPass";
        }

        Usuario usuario = userService.getUserByPasswordResetToken(token);
        if (usuario != null) {
            userService.changeUserPassword(usuario, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("password.reset.success", null, Locale.getDefault()));
            return "redirect:/Login";
        } else {
            model.addAttribute("message", messageSource.getMessage("password.reset.error.token.notfound", null, Locale.getDefault()));
            return "RestPass";
        }
    }
}
