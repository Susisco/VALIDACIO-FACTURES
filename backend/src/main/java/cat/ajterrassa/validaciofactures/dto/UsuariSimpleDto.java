package cat.ajterrassa.validaciofactures.dto;

public class UsuariSimpleDto {
    private Long id;
    private String nom;
    private String email;
    private String rol;

    public UsuariSimpleDto(Long id, String nom, String email, String rol) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.rol = rol;
    }

    public UsuariSimpleDto(cat.ajterrassa.validaciofactures.model.Usuari u) {
        this.id = u.getId();
        this.nom = u.getNom();
        this.email = u.getEmail();
        this.rol = u.getRol().name();
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }
}
