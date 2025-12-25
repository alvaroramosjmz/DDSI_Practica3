/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Usuario
 */
public class IncidenciaEjemplarDAO {
    
    private Connection conexion;

    public IncidenciaEjemplarDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // Genera un nuevo identificador de incidencia (MAX + 1)
    public int generarIdIncidencia() throws SQLException {
        String sql = "SELECT COALESCE(MAX(IDIncidencia),0) + 1 FROM INCIDENCIA_EJEMPLAR";
        PreparedStatement ps = conexion.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();
        rs.next();
        int nuevoId = rs.getInt(1);

        rs.close();
        ps.close();
        return nuevoId;
    }

    // Inserta una nueva incidencia asociada a un ejemplar
    public void insertarIncidencia(IncidenciaEjemplar incidencia) throws SQLException {
        String sql = "INSERT INTO INCIDENCIA_EJEMPLAR "
                   + "(IDIncidencia, ISBN, CodEjemplar, FechaRegistro, Descripcion, Prioridad, FechaResolucion) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setInt(1, incidencia.getIdIncidencia());
        ps.setString(2, incidencia.getIsbn());
        ps.setInt(3, incidencia.getCodEjemplar());
        ps.setDate(4, new java.sql.Date(incidencia.getFechaRegistro().getTime()));
        ps.setString(5, incidencia.getDescripcion());

        if (incidencia.getPrioridad() != null) {
            ps.setInt(6, incidencia.getPrioridad());
        } else {
            ps.setNull(6, java.sql.Types.INTEGER);
        }

        if (incidencia.getFechaResolucion() != null) {
            ps.setDate(7, new java.sql.Date(incidencia.getFechaResolucion().getTime()));
        } else {
            ps.setNull(7, java.sql.Types.DATE);
        }

        ps.executeUpdate();
        ps.close();
    }
  
}
