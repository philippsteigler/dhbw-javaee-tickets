package org.dhbw.mosbach.ai.tickets.ejb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@LocalBean
@Stateless
public class MailService {

    // Benutzername und Passwort des Gmail-Konto
    private String username = "ticketsystem.master@gmail.com";
    private String password = "mbg4ever";

    private void sendMail(String recipient, String subject, String content) {

        // Für den Aufbau der Verbindung zum Gmail SMTP Server Portnummer und Host angeben
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.auth", true);

        // Wird für Authentifizierung gegenüber Google benötigt
        Authenticator authenticator = new Authenticator() {
            private PasswordAuthentication pa = new PasswordAuthentication(username, password);

            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return pa;
            }
        };

        // Session für das Versenden der Emails initialisieren
        Session session = Session.getInstance(props, authenticator);

        // Mime Message versenden an recipient
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress("ticketsystem.master@gmail.com"));
            InternetAddress[] address = {new InternetAddress(recipient)};
            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(content);
            Transport.send(message);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }

    public void sendConfirmationMail(String recipient, String login_id, String password) {

        String content = "Herzlich Willkommen bei Ticket Master!\n"
                + "\n"
                + "Für Sie wurde erfolgreich ein Konto bei Ticket Master eingerichtet. "
                + "Wir bitten Sie das Passwort nach dem ersten Anmelden zu ändern.\n"
                + "Ticket Master: http://localhost:8080/tickets/pages/loginsuccess.xhtml\n"
                + "\n"
                + "Zugangsdaten:\n"
                + "Benutzername: " + login_id + "\n"
                + "Passwort: " + password + "\n"
                + "\n"
                + "Mit freundlichen Grüßen\n"
                + "der Admin von Ticket Master";

        sendMail(recipient, "Bestätigung Registrierung", content);
    }
}
