/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package espacios;

import java.sql.Timestamp;

/**
 *
 * @author uSer
 */
public class IncidenciaEspacio {
    private int incidenciaId;
    private int espacioId;
    private String descripcion;
    private Timestamp inicioIncidencia;
    private Timestamp finIncidencia;
    private String accionReserva; // "MANTENER" | "CANCELAR_Y_AVISAR"
    
    public IncidenciaEspacio() {}

    public IncidenciaEspacio(int incidenciaId, int espacioId, String descripcion,
                             Timestamp inicioIncidencia, Timestamp finIncidencia, String accionReserva) {
        this.incidenciaId = incidenciaId;
        this.espacioId = espacioId;
        this.descripcion = descripcion;
        this.inicioIncidencia = inicioIncidencia;
        this.finIncidencia = finIncidencia;
        this.accionReserva = accionReserva;
    }
    
    // Getters y Setters
    
    public int getIncidenciaId() {
        return incidenciaId;
    }

    public void setIncidenciaId(int incidenciaId) {
        this.incidenciaId = incidenciaId;
    }

    public int getEspacioId() {
        return espacioId;
    }

    public void setEspacioId(int espacioId) {
        this.espacioId = espacioId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getInicioIncidencia() {
        return inicioIncidencia;
    }

    public void setInicioIncidencia(Timestamp inicioIncidencia) {
        this.inicioIncidencia = inicioIncidencia;
    }

    public Timestamp getFinIncidencia() {
        return finIncidencia;
    }

    public void setFinIncidencia(Timestamp finIncidencia) {
        this.finIncidencia = finIncidencia;
    }

    public String getAccionReserva() {
        return accionReserva;
    }

    public void setAccionReserva(String accionReserva) {
        this.accionReserva = accionReserva;
    }

    @Override
    public String toString() {
        return "IncidenciaEspacio{" +
                "incidenciaId=" + incidenciaId +
                ", espacioId=" + espacioId +
                ", descripcion='" + descripcion + '\'' +
                ", inicioIncidencia=" + inicioIncidencia +
                ", finIncidencia=" + finIncidencia +
                ", accionReserva='" + accionReserva + '\'' +
                '}';
    }
}
