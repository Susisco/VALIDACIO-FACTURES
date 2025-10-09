package cat.ajterrassa.validaciofactures.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@ConditionalOnProperty(name = "debug.endpoints.enabled", havingValue = "true")
public class DebugController {

    @Value("${play.integrity.validation-enabled:false}")
    private boolean playIntegrityEnabled;

    @PostMapping("/play-integrity-token")
    public ResponseEntity<Map<String, Object>> receivePlayIntegrityToken(
            @RequestHeader(value = "X-Play-Integrity-Token", required = false) String token,
            @RequestBody(required = false) Map<String, Object> body) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("received", true);
        response.put("timestamp", System.currentTimeMillis());
        response.put("playIntegrityEnabled", playIntegrityEnabled);
        
        if (token != null) {
            response.put("tokenReceived", true);
            response.put("tokenLength", token.length());
            response.put("tokenStart", token.length() > 50 ? token.substring(0, 50) + "..." : token);
            System.out.println("=== PLAY INTEGRITY TOKEN RECEIVED ===");
            System.out.println("Token: " + token);
            System.out.println("Length: " + token.length());
            System.out.println("=====================================");
        } else {
            response.put("tokenReceived", false);
            System.out.println("=== NO PLAY INTEGRITY TOKEN IN REQUEST ===");
        }
        
        if (body != null) {
            response.put("bodyReceived", body);
        }
        
        return ResponseEntity.ok(response);
    }
}