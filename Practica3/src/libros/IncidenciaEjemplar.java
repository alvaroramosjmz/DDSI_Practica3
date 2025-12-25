/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.util.Date;
/**
 *
 * @author Usuario
 */
public class IncidenciaEjemplar {
    
    
    private int idIncidencia;
    private String isbn;
    private int codEjemplar;
    private Date fechaRegistro;
    private String descripcion;
    private Integer prioridad;
    private Date fechaResolucion;
    
    // Constructor por defecto
    public IncidenciaEjemplar() {}
    
    
    // Constructor con parámetros
    public IncidenciaEjemplar(int idIncidencia, String isbn, int codEjemplar,
                              Date fechaRegistro, String descripcion,
                              Integer prioridad, Date fechaResolucion) {
        this.idIncidencia = idIncidencia;
        this.isbn = isbn;
        this.codEjemplar = codEjemplar;
        this.fechaRegistro = fechaRegistro;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.fechaResolucion = fechaResolucion;
    }

    // getters y setters
    public int getIdIncidencia() { return idIncidencia; }
    public void setIdIncidencia(int idIncidencia) { this.idIncidencia = idIncidencia; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getCodEjemplar() { return codEjemplar; }
    public void setCodEjemplar(int codEjemplar) { this.codEjemplar = codEjemplar; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }

    public Date getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(Date fechaResolucion) { this.fechaResolucion = fechaResolucion; }

}
