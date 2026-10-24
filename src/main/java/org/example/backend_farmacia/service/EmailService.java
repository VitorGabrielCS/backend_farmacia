package org.example.backend_farmacia.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String from;
    private final boolean devLogCode;

    public EmailService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${spring.mail.username:no-reply@farmacia.local}") String from,

            @Value("${app.dev-log-code:false}") boolean devLogCode
    ) {
        this.mailSenderProvider = mailSenderProvider;
        this.from = from;
        this.devLogCode = devLogCode;
    }

    public void sendPasswordResetCode(String to, String name, String code, int minutes) {

        if (devLogCode) {
            log.warn("[DEV] Código de redefinição console para {}: {}", to, code);
        }

        JavaMailSender javaMailSender = mailSenderProvider.getIfAvailable();

        if (javaMailSender == null) {
            if (!devLogCode) {
                log.warn("SMTP não configurado. Código de redefinição para {}: {}", to, code);
            }
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("Redefinição de senha - Farmácia");
            message.setText("Olá, " + name + "!\n\n"
            + "Seu código para redefinir a sua senha é: " + code + "\n\n"
            + "Ele expira em " + minutes + " minutos.\n"
            + "Se você não pediu a redefinição, só ignore este e-mail.");
            javaMailSender.send(message);
        } catch (MailException e) {
            log.error("Falha ao enviar o e-mail de redefinição de senha para {}: {}", to, e.getMessage());

        }
    }
}
