/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.sql.Connection;// conexión activa con la DB mediante JDBC
import java.sql.SQLException; // para lanzar excepciones cuando ocurren errores SQL o de conexion
import java.sql.PreparedStatement; // para ejcutar sentencias SQL parametrizadas
import java.sql.ResultSet; // para almacenar los datos obtenidos en una consulta SQL


/**
 * DAO (Data Access Object) de la entidad IncidenciaEjemplar.
 * 
 * Esta clase encapsula las operaciones de acceso a datos relacionadas
 * con las incidencias asociadas a ejemplares de libros, permitiendo
 * su registro en la base de datos.
 * 
 * @author Usuario
 */

public class IncidenciaEjemplarDAO {
    
    private Connection conexion;
    
    // Constructor con parámetros (recibe una conexion previa JDBC)
    public IncidenciaEjemplarDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // Genera un nuevo identificador de incidencia (el identificador se obtiene 
    // como el valor máximo existente + 1)
    public int generarIdIncidencia() throws SQLException {
        
        // Consulta SQL que obtiene el mayor ID de incidencia registrado.
        String sql = "SELECT COALESCE(MAX(IDIncidencia),0) FROM INCIDENCIA_EJEMPLAR";
        
        // Se prepara la sentencia SQL
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Ejecuto la consulta y guardo el resultado
        ResultSet rs = ps.executeQuery();
        
        // Avanzo hasta la primera fila
        rs.next();
        
        // Genro nuevo identificador (max+1)
        int nuevoId = rs.getInt(1) + 1;

        // Cierro los recursos utilizado
        rs.close();
        ps.close();
        
        // Devuelvo nuevo idIncidencia
        return nuevoId;
    }

    // Inserta una nueva incidencia asociada a un ejemplar concreto
    public void insertarIncidencia(IncidenciaEjemplar incidencia) throws SQLException {
        
        // Sentencia SQL para insertar una nueva incidencia en la BD
        String sql = "INSERT INTO INCIDENCIA_EJEMPLAR "
                   + "(IDIncidencia, ISBN, CodEjemplar, FechaRegistro, Descripcion, Prioridad, FechaResolucion) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        //Preparo la sentencia 
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Asigno los valores a los parámetros
        ps.setInt(1, incidencia.getIdIncidencia());
        ps.setString(2, incidencia.getIsbn());
        ps.setInt(3, incidencia.getCodEjemplar());
        ps.setDate(4, new java.sql.Date(incidencia.getFechaRegistro().getTime()));
        ps.setString(5, incidencia.getDescripcion());
        
        // Asigno la prioridad si existe si no se almacena null
        if (incidencia.getPrioridad() != null) {
            ps.setInt(6, incidencia.getPrioridad());
        } else {
            ps.setNull(6, java.sql.Types.INTEGER);
        }
        
        // Asigno posible fecha de resolucion si existe si no se almacena null
        if (incidencia.getFechaResolucion() != null) {
            ps.setDate(7, new java.sql.Date(incidencia.getFechaResolucion().getTime()));
        } else {
            ps.setNull(7, java.sql.Types.DATE);
        }
        
        // Ejecuto la actualización
        ps.executeUpdate();
        
        // Cierro el recurso
        ps.close();
    }
  
}
