/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.sql.Connection;// conexión activa con la DB mediante JDBC
import java.sql.SQLException; // para lanzar excepciones cuando ocurren errores SQL o de conexion
import java.sql.Statement; // para enviar sentencias SQL a traves de conexion JDBC
import java.sql.PreparedStatement; // para ejcutar sentencias SQL parametrizadas
import java.sql.ResultSet; // para almacenar los datos obtenidos en una consulta SQL
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
        
        // Dejamos la sentencia SQL preparada a falta de rellenar los valores de los parámetros ? 
        String sql = "INSERT INTO LIBRO "
                   + "(ISBN, Autor, Titulo, Editorial, FechaPublicacion, NumPaginas, Edicion, Genero) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        // La BD recibe la estructura SQL, comprueba que es correcta y está 
        // preparada para recibir los valores que le faltan
        PreparedStatement ps = conexion.prepareStatement(sql);
                
        // Asigno los valores a los parámetros
        ps.setString(1, libro.getIsbn());
        ps.setString(2, libro.getAutor());
        ps.setString(3, libro.getTitulo());
        
        // editorial, fechaPublicacion, numPaginas, genero y edicion no eran obligatorios
        if (libro.getEditorial() != null){
            ps.setString(4, libro.getEditorial());
        }else{
            ps.setNull(4, java.sql.Types.VARCHAR);
        }

        if (libro.getFechaPublicacion() != null){
            ps.setDate(5, new java.sql.Date(libro.getFechaPublicacion().getTime()));
        }else{
            ps.setNull(5, java.sql.Types.DATE);
        }

        if (libro.getNumPaginas() != null){
            ps.setInt(6, libro.getNumPaginas());
        }else{
            ps.setNull(6, java.sql.Types.INTEGER);
        }
    
        if (libro.getEdicion() != null) {
            ps.setInt(7, libro.getEdicion());
        } else {
            ps.setNull(7, java.sql.Types.INTEGER);
        }
        
        if (libro.getGenero() != null) {
            ps.setString(8, libro.getGenero());
        } else {
            ps.setNull(8, java.sql.Types.VARCHAR);
        }
        
        // Ejecutamos la sentancia y cerramos el statement
        ps.executeUpdate();
        conexion.commit();
        ps.close();
    }

    
    // Comprueba si existe al menos un libro registrado en el sistema con ISBN
    // concreto(devuelve true/false)
    public boolean existeLibro(String isbn) throws SQLException {
        
        // Sentencia que cuenta cuantas filas hay en la tabla LIBRO con ese ISBN
        String sql = "SELECT COUNT(*) FROM LIBRO WHERE ISBN = ?";
        
        // Preparo la sentencia
        PreparedStatement ps = conexion.prepareStatement(sql);
        
        // Asigno el valor al parámetro
        ps.setString(1, isbn);
        
        // Ejecutamos la consulta y guardams el resultado de la consulta (1 sola fila - no más pq  pq ISBN es PK )
        ResultSet rs = ps.executeQuery();
        
        // Avanzamos a la primera fila (ResultSet empieza antes de la primera fila)
        rs.next();
        boolean existe = rs.getInt(1) > 0; // getInt(1) obtiene la 1ª columna de la consulta
        
        // Cierro los recursos utilizados
        rs.close();
        ps.close();
        
        // Devuelva resultado de la comprobacion (1 si existe 0 si no)
        return existe;
    }

    // Obtiene la lista de libros registrados en el sistema aplicando filtros opcionales
    public List<Libro> listarLibros(String isbn,String autor,String titulo,String genero) throws SQLException {

        // Lista vacia que se ira rellenando con los libros recuperados de la BD
        List<Libro> libros = new ArrayList<>();

        // Construimos dinamicamente la sentencia SQL en funcion de los filtros indicados
        StringBuilder sql = new StringBuilder(
            "SELECT ISBN, Autor, Titulo, Editorial, FechaPublicacion, NumPaginas, Edicion, Genero " +
            "FROM LIBRO WHERE 1=1"
        );

        // Lista auxiliar para almacenar los valores de los parametros de la consulta
        List<Object> parametros = new ArrayList<>();

        // Si se indica ISBN, se anade el filtro correspondiente
        if (isbn != null && !isbn.isBlank()) {
            sql.append(" AND ISBN = ?");
            parametros.add(isbn);
        }

        // Si se indica autor, se filtra por coincidencia parcial
        if (autor != null && !autor.isBlank()) {
            sql.append(" AND Autor LIKE ?");
            parametros.add("%" + autor + "%");
        }

        // Si se indica titulo, se filtra por coincidencia parcial
        if (titulo != null && !titulo.isBlank()) {
            sql.append(" AND Titulo LIKE ?");
            parametros.add("%" + titulo + "%");
        }

        // Si se indica genero, se filtra por coincidencia parcial
        if (genero != null && !genero.isBlank()) {
            sql.append(" AND Genero LIKE ?");
            parametros.add("%" + genero + "%");
        }

        // Preparo la sentencia SQL final usando PreparedStatement
        PreparedStatement ps = conexion.prepareStatement(sql.toString());

        // Asigno los valores a los parametros de la consulta en el orden correspondiente
        for (int i = 0; i < parametros.size(); i++) {
            ps.setObject(i + 1, parametros.get(i));
        }

        // Ejecuto la consulta y guardo el resultado
        ResultSet rs = ps.executeQuery();

        // Recorremos todas las filas del resultado de la consulta
        while (rs.next()) {
            // Creamos un objeto tipo Libro con los datos recuperados (cada col SQL --> atributo Java)
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

        // Cierro los recursos utilizados
        rs.close();
        ps.close();

        // Devuelvo la lista de libros que cumplen los criterios indicados
        return libros;
    }

}
