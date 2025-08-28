package cat.ajterrassa.validaciofactures.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Component
public class PasswordUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
    private static final int DEFAULT_LENGTH = 10;
    private final SecureRandom random = new SecureRandom();

    public String generateRandomPassword() {
        return generateRandomPassword(DEFAULT_LENGTH);
    }

    public String generateRandomPassword(int length) {
        return random.ints(length, 0, CHARACTERS.length())
            .mapToObj(i -> String.valueOf(CHARACTERS.charAt(i)))
            .collect(Collectors.joining());
    }
}
