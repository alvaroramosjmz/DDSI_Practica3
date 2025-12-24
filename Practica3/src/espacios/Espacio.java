/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package espacios;


public class Espacio {
     private int espacioId;
    private String nombre;
    private String tipo;
    private int capacidad;
    private String estado; // "BLOQUEADO" | "NO BLOQUEADO"

    public Espacio() {
    }
    
    public Espacio(int espacioId, String nombre, String tipo, int capacidad, String estado) {
        this.espacioId = espacioId;
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    // Getters y Setters
    public int getEspacioId() {
        return espacioId;
    }

    public void setEspacioId(int espacioId) {
        this.espacioId = espacioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Para mostrar en listados y depuración.
     */
    @Override
    public String toString() {
        return "Espacio{" +
                "espacioId=" + espacioId +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", capacidad=" + capacidad +
                ", estado='" + estado + '\'' +
                '}';
    }
}
