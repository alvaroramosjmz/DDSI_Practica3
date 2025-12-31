/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.sql.Connection; // conexión activa con la DB mediante JDBC
import java.sql.SQLException; // para lanzar excepciones cuando ocurren errores SQL o de conexion
import java.sql.PreparedStatement; // para ejcutar sentencias SQL parametrizadas
import java.sql.ResultSet; // para almacenar los datos obtenidos en una consulta SQL


/**
 *
 * DAO (Data Access Object) de la entidad Ejemplar.
 * 
 * Esta clase encapsula todas las operaciones de acceso a datos relacionadas 
 * con la tabla EJEMPLAR, permitiendo gestionar el alta, consulta y cambio de 
 * estado de los ejemplares
 * 
 * 
 * @author Usuario
 */
public class EjemplarDAO {
    
    private Connection conexion;
    
    
    // Constructor con parámetros (recibe una conexion JDBC previa)
    public EjemplarDAO(Connection conexion) {
        this.conexion = conexion;
    }

    //Devuelve el ultimo CodEjemplar generado para un ISBN
    public int obtenerUltimoCodEjemplar(String isbn) throws SQLException {
       
       // Sentencia SQL consulta ultimo codJemplar generado
       String sql = "SELECT MAX(CodEjemplar) AS ultimo FROM EJEMPLAR WHERE ISBN = ?";
       
       // Preparo la sentencia
       PreparedStatement ps = conexion.prepareStatement(sql);
       
       // Asigno el valor al parámetro
       ps.setString(1, isbn);
       
       // Ejecuto la consulta y guardo el resultado
       ResultSet rs = ps.executeQuery();
       
       // Avanzo hasta la primera fila
       rs.next();
       
       // Obtengo el ultimo codEJemplar
       int cod = rs.getInt("ultimo");
       
       // Cierro los recursos
       rs.close();
       ps.close();
       
       // Devuelvo el código
       return cod;
    }

    // Inserta un nuevo ejemplar en la BD con el estado que se indique
    public void insertarEjemplar(String isbn) throws SQLException {
        
        // Sentencia SQL DE inserción de un nuevo ejemplar
        String sql = "INSERT INTO EJEMPLAR (ISBN) VALUES (?)";
        
        // Preparo la sentencia
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Asigno valor al parámetro
        ps.setString(1, isbn);
        // Ejecuto la inserción
        ps.executeUpdate();
        
        conexion.commit();
        
        // Cierro el recurso utilizado
        ps.close();
    }

    // Obtiene el codEjemplar disponible de un libro (el primero que encuentre)
    // devuelve null si no hay ninguno
    public Integer obtenerEjemplarDisponible(String isbn) throws SQLException {
        
        // Consulta SQL que busca ejemplares en estado DISPONIBLE
        String sql = "SELECT CodEjemplar FROM EJEMPLAR WHERE ISBN = ? AND Estado = 'DISPONIBLE'";
        
        // Preparo la sentencia
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Asigno valor al parámetro
        ps.setString(1, isbn);
        
        // Ejecuto la consulta
        ResultSet rs = ps.executeQuery();
        
        // Variable que almacenará el código encontrado (si no hay se queda como null)
        Integer cod = null;
        
        // Si existe ejempalr con ese ISBN, se convierte el codEjemplar 
        if (rs.next()) {
            cod = rs.getInt("CodEjemplar");
        }
        
        // Se cierran los recursos
        rs.close();
        ps.close();
        
        // Devuelvo el código del priemr ejemplar disponible o null si no hay
        return cod;
    }

    // Cambia el estado de un ejemplar concreto (baja lógica o cambio de disponibilidad)
    public void cambiarEstadoEjemplar(String isbn, int codEjemplar, EstadoEjemplar estado) throws SQLException {
        
        // Sentencia SQL para actualizar el estado del ejemplar
        String sql = "UPDATE EJEMPLAR SET Estado = ? WHERE ISBN = ? AND CodEjemplar = ?";
        
        // Preparo la sentencia
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Asigno valores a los parámetros 
        ps.setString(1, estado.name());
        ps.setString(2, isbn);
        ps.setInt(3, codEjemplar);
        
        // Ejecuto la actualización
        ps.executeUpdate();
        
        conexion.commit();
        
        // Cierro el recurso utilizado
        ps.close();
    }

    // Comprueba si existe un ejemplar concreto (ISBN + CodEjemplar)
    public boolean existeEjemplar(String isbn, int codEjemplar) throws SQLException {
        
         // Consulta SQL que cuenta los ejemplares con ese ISBN y código
        String sql = "SELECT COUNT(*) FROM EJEMPLAR WHERE ISBN = ? AND CodEjemplar = ?";
        
        // Se prepara la sentencia
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Se asignan los valores a los parámetros
        ps.setString(1, isbn);
        ps.setInt(2, codEjemplar);
        
        // Ejecuto la sentencia y guardo el resultado
        ResultSet rs = ps.executeQuery();
        
        // Avanzo a la primera fila
        rs.next();
        
        // Si el resultado de la primera col de la consulta es mayor que 0 el ejemplar existe
        boolean existe = rs.getInt(1) > 0;
        
        // Cierro los recusos utilizados
        rs.close();
        ps.close();
        
        // Devuelvo resultado de la comprobación
        return existe;
    }

    // Obtiene el estado actual de un ejemplar (devuelve el estado null si el ejemplar no existe)
    public EstadoEjemplar obtenerEstadoEjemplar(String isbn, int codEjemplar) throws SQLException {
        
        // Consulta SQL para obtener el estado del ejemplar
        String sql = "SELECT Estado FROM EJEMPLAR WHERE ISBN = ? AND CodEjemplar = ?";
        
        // Se prepara la sentencia
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Asigno los valores a los parámetros 
        ps.setString(1, isbn);
        ps.setInt(2, codEjemplar);
        
        // Se ejecuta la consulta
        ResultSet rs = ps.executeQuery();
        
        // Variable dnd se almacenará el estado 
        EstadoEjemplar estado = null;
        
        // Si existe el ejemplar, se convierte el texto al enum que corresponda
        if (rs.next()) {
            estado = EstadoEjemplar.valueOf(rs.getString("Estado"));
        }
        
        // Cierro los recursos 
        rs.close();
        ps.close();
        
        // Devuelvo estado obtenido (si no existe el ejemplar devuelve null)
        return estado;
    }
    
    // Devuelve el número total de ejemplares asociados a un libro
    public int contarEjemplares(String isbn) throws SQLException {

        //Sentencia SQL que cuenta todos los ejemplares de un ISBN
        String sql = "SELECT COUNT(*) FROM EJEMPLAR WHERE ISBN = ?";

        //Preparamos la consulta
        PreparedStatement ps = conexion.prepareStatement(sql);

        //Asignamos el ISBN al parámetro
        ps.setString(1, isbn);

        //Ejecutamos la consulta y almacenamos el resultado
        ResultSet rs = ps.executeQuery();

        //Avanzamos hasta la primera fila del resultado
        rs.next();

        //Obtenemos el valor de la consulta
        int total = rs.getInt(1);

        //Cerramos recursos
        rs.close();
        ps.close();

        //Devolvemos el número total de ejemplares
        return total;
    }
    
    // Devuelve el número de ejemplares DISPONIBLES de un libro
    public int contarEjemplaresDisponibles(String isbn) throws SQLException {

        //Consulta SQL que cuenta solo los ejemplares en estado DISPONIBLE
        String sql = "SELECT COUNT(*) FROM EJEMPLAR WHERE ISBN = ? AND Estado = 'DISPONIBLE'";

        //Preparamos la sentencia
        PreparedStatement ps = conexion.prepareStatement(sql);

        //Asociamos el ISBN al parámetro
        ps.setString(1, isbn);

        //Ejecutamos la consulta y guardamos el resultado
        ResultSet rs = ps.executeQuery();

        //Accedemos a la primera fila
        rs.next();

        //Leemos el número de ejemplares disponibles
        int disponibles = rs.getInt(1);

        //Cerramos recursos
        rs.close();
        ps.close();

        //Devolvemos el resultado
        return disponibles;
    }



}
