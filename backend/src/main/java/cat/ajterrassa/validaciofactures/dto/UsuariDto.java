package cat.ajterrassa.validaciofactures.dto;



public class UsuariDto {
    private Long id;
    private String nom;
    private String email;
    private String contrasenya;
    private String rol;
    
    public void setId(Long id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public String getRol() {
        return rol;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
