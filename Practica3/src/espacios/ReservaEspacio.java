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
public class ReservaEspacio {
    
    private int reservaId;
    private int usuarioId;
    private int espacioId;
    private Timestamp inicio;
    private Timestamp fin;
    private String estadoReserva; // "CONFIRMADA" | "CANCELADA" | "CANCELADA_INCIDENCIA"

    public ReservaEspacio() {}

    public ReservaEspacio(int reservaId, int usuarioId, int espacioId, Timestamp inicio, Timestamp fin, String estadoReserva) {
        
        this.reservaId = reservaId;
        this.usuarioId = usuarioId;
        this.espacioId = espacioId;
        this.inicio = inicio;
        this.fin = fin;
        this.estadoReserva = estadoReserva;
    }
    
    
    // Getters y Setters
    
    public int getReservaId() { return reservaId; }
    public void setReservaId(int reservaId) { this.reservaId = reservaId; }
    
    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getEspacioId() {
        return espacioId;
    }

    public void setEspacioId(int espacioId) {
        this.espacioId = espacioId;
    }

    public Timestamp getInicio() {
        return inicio;
    }

    public void setInicio(Timestamp inicio) {
        this.inicio = inicio;
    }

    public Timestamp getFin() {
        return fin;
    }

    public void setFin(Timestamp fin) {
        this.fin = fin;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    @Override
    public String toString() {
        return "ReservaEspacio{" +
                "usuarioId=" + usuarioId +
                ", espacioId=" + espacioId +
                ", inicio=" + inicio +
                ", fin=" + fin +
                ", estadoReserva='" + estadoReserva + '\'' +
                '}';
    }
}
