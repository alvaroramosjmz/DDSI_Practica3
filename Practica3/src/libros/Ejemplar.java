/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

/**
 * Clase Ejemplar
 * 
 * Representa un ejemplar físico dentro del inventario de la biblioteca. Cada
 * ejemplar está asociado a un libro mediante su ISBN y se identifica de forma
 * única por el código de ejemplar. 
 * 
 * Cada ejemplar tiene un estado que indica su disponibilidad (disponible,
 * no_disponible o descatalogado).
 *
 * @author Usuario
 */
public class Ejemplar {
    
    private String isbn;
    private int codEjemplar;
    private EstadoEjemplar estado; // puede ser {disponible, no_disponible, descatalogado}
    
    
    // Constructor por defecto
    public Ejemplar() {}
    
    
    // Constructor con parámetros
    public Ejemplar(String isbn, int codEjemplar, EstadoEjemplar estado) {
        this.isbn = isbn;
        this.codEjemplar = codEjemplar;
        this.estado = estado;
    }

    // Getters y Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getCodEjemplar() { return codEjemplar; }
    public void setCodEjemplar(int codEjemplar) { this.codEjemplar = codEjemplar; }

    public EstadoEjemplar getEstado() { return estado; }
    public void setEstado(EstadoEjemplar estado) { this.estado = estado; }

}
