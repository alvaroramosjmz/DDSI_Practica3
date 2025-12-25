/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.sql.Connection;// conexión activa con la DB
import java.sql.SQLException; //// para lanzar excepciones cuando ocurren errores SQL o de conexion
import java.sql.Statement; // para enviar sentencias SQL a traves de conexion JDBC
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) encargado de gestionar el acceso a datos
 * de la entidad Libro.
 * 
 * Esta clase encapsula todas las operaciones de acceso a la base de datos
 * relacionadas con la tabla LIBRO, aislando la lógica SQL del resto del sistema.
 *
 * @author Usuario
 */

public class LibroDAO {
    
    // Conexión actual activa con la BD
    private Connection conexion;

    
    // Constructor con parámetros (conexion JDBC previamente creada)
    public LibroDAO(Connection conexion) {
        this.conexion = conexion;
    }

    
    // Inseta un nuevo libro en la BD
    public void insertarLibro(Libro libro) throws SQLException {
        String sql = "INSERT INTO LIBRO "
                   + "(ISBN, Autor, Titulo, Editorial, FechaPublicacion, NumPaginas, Edicion, Genero) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, libro.getIsbn());
        ps.setString(2, libro.getAutor());
        ps.setString(3, libro.getTitulo());
        ps.setString(4, libro.getEditorial());
        ps.setDate(5, new java.sql.Date(libro.getFechaPublicacion().getTime()));
        ps.setInt(6, libro.getNumPaginas());
        ps.setInt(7, libro.getEdicion());
        ps.setString(8, libro.getGenero());

        ps.executeUpdate();
        ps.close();
    }

    
    // Comprueba si existe un libro registrado en el sistema a partir de su ISBN
    // (devuelve true/false)
    
    public boolean existeLibro(String isbn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM LIBRO WHERE ISBN = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, isbn);

        ResultSet rs = ps.executeQuery();
        rs.next();
        boolean existe = rs.getInt(1) > 0;

        rs.close();
        ps.close();
        return existe;
    }

    //Obtiene la lista completa de libros registrados en el sistema.
    public List<Libro> listarLibros() throws SQLException {
        List<Libro> libros = new ArrayList<>();

        String sql = "SELECT * FROM LIBRO";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Libro libro = new Libro(
                rs.getString("ISBN"),
                rs.getString("Autor"),
                rs.getString("Titulo"),
                rs.getString("Editorial"),
                rs.getDate("FechaPublicacion"),
                rs.getInt("NumPaginas"),
                rs.getInt("Edicion"),
                rs.getString("Genero")
            );
            libros.add(libro);
        }

        rs.close();
        st.close();
        return libros;
    }
}
