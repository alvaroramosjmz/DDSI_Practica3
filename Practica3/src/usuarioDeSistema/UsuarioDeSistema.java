package usuarioDeSistema;

public class UsuarioDeSistema {
    private int id;
    private String email;
    private String nombre;
    private String telefono;
    private boolean esAdmin;
    private boolean esMalicioso;

    public UsuarioDeSistema(int id, String email, String nombre, String telefono, boolean esAdmin, boolean esMalicioso) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.esAdmin = esAdmin;
        this.esMalicioso = esMalicioso;
    }

    public boolean esAdmin() { return esAdmin; }
    public boolean esMalicioso() { return esMalicioso; }
    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public int getId() { return id; }
    public String getRol() {
        return esAdmin ? "ADMINISTRADOR" : "BIBLIOTECARIO";
    }
}