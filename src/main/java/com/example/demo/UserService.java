package com.example.demo;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.ZonedDateTime;
import java.time.ZoneId;

@Transactional
@Service
public class UserService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(PasswordResetTokenRepository tokenRepository, UsuarioRepository usuarioRepository,
                       BCryptPasswordEncoder passwordEncoder, EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;

    }
    @Transactional
    public String registerUser(Usuario newUser) {
        Optional<Usuario> existingUser = usuarioRepository.findByCedula(newUser.getCedula());
        if (existingUser.isPresent()) {
            return "usuario ya registrado con esta cedula";
        } else {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            usuarioRepository.save(newUser);

            emailService.sendRegistroNotificationEmail(newUser);

            return "registro exitoso";
        }
    }

    public void createPasswordResetTokenForUser(Usuario usuario, String token) {
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setUsuario(usuario);
        myToken.setToken(token);
        ZonedDateTime expiryDate = ZonedDateTime.now(ZoneId.of("America/Lima")).plusMinutes(20);
        myToken.setExpiryDate(expiryDate);

        tokenRepository.save(myToken);
        System.out.println("Hora de generación del token: " + ZonedDateTime.now(ZoneId.of("America/Lima")));

    }

    public String validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null) {
            return "Token no válido.";
        }
        if (resetToken.getExpiryDate().isBefore(ZonedDateTime.now())) {
            return "Token expirado.";
        }

        return null;
    }

    public Usuario getUserByPasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken != null && resetToken.getExpiryDate().isAfter(ZonedDateTime.now())) {
            return resetToken.getUsuario();
        }
        return null;
    }

    public void changeUserPassword(Usuario usuario, String newPassword) {
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
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
            throw new Exception("Usuario no encontrado");
        }
    }
}
