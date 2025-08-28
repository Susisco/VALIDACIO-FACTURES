package cat.ajterrassa.validaciofactures.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

   public void sendPasswordEmail(String to, String password) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Contrasenya temporal d'accés");
        message.setText(
            "Hola,\n\n" +
            "La teva nova contrasenya temporal és: " + password + "\n\n" +
            "Si us plau, inicia sessió i canvia la contrasenya.\n\n" +
            "Gràcies."
        );
        message.setFrom("noreply_ajterrassa@gmail.com"); // important!
        mailSender.send(message);
    } catch (Exception e) {
        System.err.println("❌ Error enviant el correu: " + e.getMessage());
        e.printStackTrace(); // ajuda per veure l'error real
    }
}

}
