package cat.ajterrassa.validaciofactures.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlayIntegrityService {

    private static final Logger logger = LoggerFactory.getLogger(PlayIntegrityService.class);

    private final boolean validationEnabled;
    private final Set<String> acceptedTokens;

    public PlayIntegrityService(
            @Value("${play.integrity.validation-enabled:true}") boolean validationEnabled,
            @Value("${play.integrity.accepted-tokens:}") String acceptedTokens
    ) {
        this(validationEnabled, parseTokens(acceptedTokens));
    }

    public PlayIntegrityService(boolean validationEnabled, Collection<String> acceptedTokens) {
        this.validationEnabled = validationEnabled;
        this.acceptedTokens = acceptedTokens == null ? Collections.emptySet() : acceptedTokens.stream()
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean validateToken(String token) {
        if (!validationEnabled) {
            logger.debug("Play Integrity validation is disabled. Allowing request by configuration.");
            return true;
        }
        if (token == null || token.isBlank()) {
            logger.warn("Rejected request without Play Integrity token");
            return false;
        }
        if (acceptedTokens.isEmpty()) {
            logger.warn("No accepted Play Integrity tokens configured; rejecting request");
            return false;
        }
        boolean valid = acceptedTokens.contains(token);
        if (!valid) {
            logger.warn("Rejected request with unknown Play Integrity token (hash: {})",
                    Integer.toHexString(token.hashCode()));
        }
        return valid;
    }

    private static Collection<String> parseTokens(String acceptedTokens) {
        if (acceptedTokens == null || acceptedTokens.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(acceptedTokens.split(","))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toSet());
    }
}
