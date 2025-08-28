package cat.ajterrassa.validaciofactures.dto;

public class LoginResponse {
    private String token;
    private String nom;
    private Long id;
    private boolean contrasenyaTemporal;
        private String rol; 


    public LoginResponse(String token, String nom, Long id, boolean contrasenyaTemporal, String rol) {
        this.token = token;
        this.nom = nom;
        this.id = id;
        this.contrasenyaTemporal = contrasenyaTemporal;
        this.rol = rol; // Per defecte, pots canviar-ho si cal 
    }

    // Getters i setters
    public String getToken() { return token; }
    public String getNom() { return nom; }
    public Long getId() { return id; }
    public boolean isContrasenyaTemporal() { return contrasenyaTemporal; }
    public String getRol() { return rol; }

    public void setToken(String token) { this.token = token; }
    public void setNom(String nom) { this.nom = nom; }
    public void setId(Long id) { this.id = id; }
    public void setContrasenyaTemporal(boolean contrasenyaTemporal) { this.contrasenyaTemporal = contrasenyaTemporal; }
    public void setRol(String rol) { this.rol = rol;

    }
    }
    
