package com.example.demo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;
import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Autowired
    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine,
                        MessageSource messageSource) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    public void sendRegistroNotificationEmail(Usuario usuario) throws MessagingException {
        String subject = messageSource.getMessage("registration.email.subject", null, Locale.getDefault());
        Context context = new Context();
        context.setVariable("name", usuario.getPrimerNombre());
        String htmlContent = templateEngine.process("registroNotificationTemplate", context);

        sendEmail(usuario.getEmail(), subject, htmlContent);
    }

    public void sendLoginNotificationEmail(Usuario usuario) throws MessagingException {
        String subject = messageSource.getMessage("login.notification.subject", null, Locale.getDefault());
        Context context = new Context();
        context.setVariable("name", usuario.getPrimerNombre());
        String htmlContent = templateEngine.process("loginNotificationTemplate", context);

        sendEmail(usuario.getEmail(), subject, htmlContent);
    }

    public void sendResetTokenEmail(String contextPath, String token, Usuario usuario) throws MessagingException {
        String url = contextPath + "/RestPass?token=" + token;
        String subject = messageSource.getMessage("password.reset.email.subject", null, Locale.getDefault());
        Context context = new Context();
        context.setVariable("name", usuario.getPrimerNombre());
        context.setVariable("url", url);
        String htmlContent = templateEngine.process("resetPasswordTemplate", context);

        sendEmail(usuario.getEmail(), subject, htmlContent);
    }

    public void sendUpdateNotificationEmail(Usuario usuario) throws jakarta.mail.MessagingException {
        Map<String, Object> variables = Map.of("name", usuario.getPrimerNombre());
        sendEmailWithTemplate(usuario.getEmail(), "Actualización de Datos", "updateNotificationTemplate", variables);
    }

    public void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> variables) throws jakarta.mail.MessagingException {
        jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(templateName, context);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }



    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        mailSender.send(message);
    }
}
