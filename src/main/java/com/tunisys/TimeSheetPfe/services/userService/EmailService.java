package com.tunisys.TimeSheetPfe.services.userService;

import com.tunisys.TimeSheetPfe.exceptions.EmailNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendEmail(String from ,String to, String subject, String firstname, String email, String password) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);

        Context context = new Context();
        context.setVariable("firstname", firstname);
        context.setVariable("email", email);
        context.setVariable("password", password);

        String htmlContent = templateEngine.process("email-template", context);

        helper.setText(htmlContent, true);


        try {
            mailSender.send(message);
        } catch (Exception e) {
            if (e.getCause() instanceof jakarta.mail.SendFailedException) {
                throw new EmailNotFoundException("The from email address is not found: " + from);
            }
            throw e;
        }

    }

    public void sendResetPassword(String from, String to, String subject, String name, String email, String password) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("email", email);
        context.setVariable("newPassword", password);

        String htmlContent = templateEngine.process("reset-password-email", context);

        helper.setText(htmlContent, true);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            if (e.getCause() instanceof jakarta.mail.SendFailedException) {
                throw new EmailNotFoundException("The from email address is not found: " + from);
            }
            throw e;
        }
    }

}
