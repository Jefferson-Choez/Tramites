package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetTokenEmail(String contextPath, String token, Usuario usuario) {
        String url = contextPath + "/RestPass?token=" + token;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(usuario.getEmail());
        email.setSubject("Restablecimiento de contraseña");
        email.setText("Para restablecer su contraseña, haga clic en el siguiente enlace: " + url);
        mailSender.send(email);

    }
    public void sendRegistroNotificationEmail(Usuario usuario) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(usuario.getEmail());
        email.setSubject("Registro Exitoso");
        email.setText("Hola, " + usuario.getPrimerNombre() + " " + usuario.getPrimerApellido() + ",\n\n" +
                "Te has registrado exitosamente en EMAPAPC-EP Tramites.\n\n" +
                "Saludos,\n");
        mailSender.send(email);
    }

    public void sendLoginNotificationEmail(Usuario usuario) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(usuario.getEmail());
        email.setSubject("Notificación de Inicio de Sesión");
        email.setText("Hola, " + usuario.getPrimerNombre() + " " + usuario.getPrimerApellido() + " " +
                "has iniciado sesión con éxito en EMAPAPC-EP Tramites.");
        mailSender.send(email);
    }
    public void sendUpdateNotificationEmail(Usuario usuario) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(usuario.getEmail());
        email.setSubject("Actualización de Datos");
        email.setText("Hola, " + usuario.getPrimerNombre() + " " + usuario.getPrimerApellido() + ",\n\n" +
                "Tus datos han sido actualizados exitosamente.\n\n" +
                "Saludos,\n");
        mailSender.send(email);
    }
}
