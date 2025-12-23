/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lectores;

public class Lector {
    private int id;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String esMalicioso; // 'S' o 'N'

    // Constructor vacío
    public Lector() {}

    // Constructor completo
    public Lector(int id, String nombre, String apellidos, String telefono, String esMalicioso) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.esMalicioso = esMalicioso;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEsMalicioso() { return esMalicioso; }
    public void setEsMalicioso(String esMalicioso) { this.esMalicioso = esMalicioso; }
}
