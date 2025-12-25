/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

/**
 *
 * @author Usuario
 */
public class Ejemplar {
    
    private String isbn;
    private int codEjemplar;
    private EstadoEjemplar estado;
    
    
    // Constructor por defecto
    public Ejemplar() {}
    
    
    // Constructor con parámetros
    public Ejemplar(String isbn, int codEjemplar, EstadoEjemplar estado) {
        this.isbn = isbn;
        this.codEjemplar = codEjemplar;
        this.estado = estado;
    }

    // getters y setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getCodEjemplar() { return codEjemplar; }
    public void setCodEjemplar(int codEjemplar) { this.codEjemplar = codEjemplar; }

    public EstadoEjemplar getEstado() { return estado; }
    public void setEstado(EstadoEjemplar estado) { this.estado = estado; }

}
