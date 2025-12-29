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
    
    // Inserta una nueva incidencia asociada a un ejemplar concreto
    public void insertarIncidencia(IncidenciaEjemplar incidencia) throws SQLException {
        
        // Sentencia SQL para insertar una nueva incidencia en la BD
        String sql = "INSERT INTO INCIDENCIA_EJEMPLAR "
                   + "(ISBN, CodEjemplar, Descripcion, Prioridad, FechaResolucion) "
                   + "VALUES (?, ?, ?, ?, ?)";
        
        //Preparo la sentencia 
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Asigno los valores a los parámetros
        ps.setString(1, incidencia.getIsbn());
        ps.setInt(2, incidencia.getCodEjemplar());
        ps.setString(3, incidencia.getDescripcion());
        
        // Asigno la prioridad si existe si no se almacena null
        if (incidencia.getPrioridad() != null) {
            ps.setInt(4, incidencia.getPrioridad());
        } else {
            ps.setNull(4, java.sql.Types.INTEGER);
        }
        
        // Asigno posible fecha de resolucion si existe si no se almacena null
        if (incidencia.getFechaResolucion() != null) {
            ps.setDate(5, new java.sql.Date(incidencia.getFechaResolucion().getTime()));
        } else {
            ps.setNull(5, java.sql.Types.DATE);
        }
        
        // Ejecuto la actualización
        ps.executeUpdate();
        
        // Cierro el recurso
        ps.close();
    }
    
    //Devuelve el ultimo idIncidencia generado 
    public int obtenerUltimoIdIncidencia() throws SQLException {
       
       // Sentencia SQL consulta ultimo codJemplar generado
       String sql = "SELECT MAX(IDIncidencia) AS ultimo FROM INCIDENCIA_EJEMPLAR";
       
       // Preparo la sentencia
       PreparedStatement ps = conexion.prepareStatement(sql);
       
       // Ejecuto la consulta y guardo el resultado
       ResultSet rs = ps.executeQuery();
       
       // Avanzo hasta la primera fila
       rs.next();
       
       // Obtengo el ultimo IDIncidencia
       int id = rs.getInt("ultimo");
       
       // Cierro los recursos
       rs.close();
       ps.close();
       
       // Devuelvo el id
       return id;
    }

}
