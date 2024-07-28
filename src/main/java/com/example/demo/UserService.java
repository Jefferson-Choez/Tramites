package com.example.demo;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
public class UserService {

    private static final int EXPIRATION_MINUTES = 30;
    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final EmailService emailService;

    @Autowired
    public UserService(PasswordResetTokenRepository tokenRepository, UsuarioRepository usuarioRepository,
                       BCryptPasswordEncoder passwordEncoder, MessageSource messageSource, EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
        this.emailService = emailService;
    }

    public String registerUser(Usuario newUser) {
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(newUser.getEmail());
        Optional<Usuario> existingUserByCedula = usuarioRepository.findByCedula(newUser.getCedula());

        if (existingUser.isPresent()) {
            return messageSource.getMessage("registration.error.email.exists", null, LocaleContextHolder.getLocale());
        } else if (existingUserByCedula.isPresent()) {
            return messageSource.getMessage("registration.error.cedula.exists", null, LocaleContextHolder.getLocale());
        } else {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            newUser.setRol("USER");
            usuarioRepository.save(newUser);
            return messageSource.getMessage("user.register.success", null, LocaleContextHolder.getLocale());
        }
    }

    public void createPasswordResetTokenForUser(Usuario usuario, String token) {
        tokenRepository.deleteByUsuario(usuario);
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setUsuario(usuario);
        myToken.setToken(token);
        myToken.setExpiryDate(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
        tokenRepository.save(myToken);

        tokenRepository.save(myToken);

    }

    public String validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null) {
            return messageSource.getMessage("token.invalid", null, LocaleContextHolder.getLocale());
        }
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return messageSource.getMessage("token.expired", null, LocaleContextHolder.getLocale());
        }
        return null;
    }

    public Usuario getUserByPasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken != null && resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            return resetToken.getUsuario();
        }
        return null;
    }

    public void changeUserPassword(Usuario usuario, String newPassword) {
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    public Usuario findByEmail(String email) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findByEmail(email);
        return optionalUsuario.orElse(null);
    }

    public void actualizarUsuario(Usuario usuario) throws Exception {
        Optional<Usuario> existingUserOptional = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingUserOptional.isPresent()) {
            Usuario existingUser = existingUserOptional.get();

            if (usuario.getPrimerNombre() != null) existingUser.setPrimerNombre(usuario.getPrimerNombre());
            if (usuario.getSegundoNombre() != null) existingUser.setSegundoNombre(usuario.getSegundoNombre());
            if (usuario.getPrimerApellido() != null) existingUser.setPrimerApellido(usuario.getPrimerApellido());
            if (usuario.getSegundoApellido() != null) existingUser.setSegundoApellido(usuario.getSegundoApellido());
            if (usuario.getCedula() != null) existingUser.setCedula(usuario.getCedula());
            if (usuario.getDirection() != null) existingUser.setDirection(usuario.getDirection());
            if (usuario.getParroquia() != null) existingUser.setParroquia(usuario.getParroquia());
            if (usuario.getTelefono() != null) existingUser.setTelefono(usuario.getTelefono());

            usuarioRepository.save(existingUser);

            emailService.sendUpdateNotificationEmail(existingUser);
        } else {
            throw new Exception(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale()));
        }
    }

    public Usuario findByEmailOrCedula(String search) {
        return usuarioRepository.findByEmailOrCedula(search)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())));
    }

    public void assignRoleToUser(Long userId, String role, String departamento) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())));
        usuario.setRol(role);
        usuario.setDepartamento(departamento);
        usuarioRepository.save(usuario);
    }

    public Usuario findById(Long userId) {
        return usuarioRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale())));
    }
}
