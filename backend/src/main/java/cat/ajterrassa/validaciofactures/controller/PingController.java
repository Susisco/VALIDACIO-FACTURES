package cat.ajterrassa.validaciofactures.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class PingController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                return ResponseEntity.ok("✅ Connexió a la base de dades OK");
            } else {
                return ResponseEntity.status(500).body("⚠️ Connexió no vàlida");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error de connexió: " + e.getMessage());
        }
    }
}
