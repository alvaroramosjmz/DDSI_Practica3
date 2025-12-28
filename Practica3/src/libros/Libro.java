/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.util.Date; //representa fecha y hora (facilmente convertible en SQL)

/**
 * Clase que representa la entidad Libro del sistema.
 * Contiene los datos básicos de un libro almacenado en la biblioteca
 * 
 * No incluye lógica ni validaciones (se realizan en la capa de servicio no 
 * en el modelo)
 * 
 * @author Usuario
 */
public class Libro {
    
    
    private String isbn;
    private String autor;
    private String titulo;
    private String editorial;
    private Date fechaPublicacion;
    private Integer numPaginas;
    private Integer edicion;
    private String genero;
    
    // Constructor por defecto
    public Libro() {}
    
    // Constructor con parámetros 
    public Libro(String isbn, String autor, String titulo, String editorial,
                 Date fechaPublicacion, Integer numPaginas, Integer edicion, String genero) {
        this.isbn = isbn;
        this.autor = autor;
        this.titulo = titulo;
        this.editorial = editorial;
        this.fechaPublicacion = fechaPublicacion;
        this.numPaginas = numPaginas;
        this.edicion = edicion;
        this.genero = genero;
    }
    
    // Getters y setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public Date getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Date fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public Integer getNumPaginas() { return numPaginas; }
    public void setNumPaginas(int numPaginas) { this.numPaginas = numPaginas; }

    public Integer getEdicion() { return edicion; }
    public void setEdicion(int edicion) { this.edicion = edicion; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }


}
