/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;      // Conexión JDBC con la base de datos
import java.sql.SQLException;    // Manejo de errores SQL
import java.sql.Statement;       // Ejecución de sentencias SQL


/**
 * TableManager del subsistema de control de libros.
 * 
 *  Se encarga de crear las tablas necesarias para:
 *    - Libros
 *    - Ejemplares
 *    - Incidencias de ejemplares

 * @author Usuario
 */
public class TableManagerLibros {
    // Conexión activa con la base de datos
    private Connection conexion;

    // Constructor
    public TableManagerLibros(Connection conexion) {
        this.conexion = conexion;
    }

    // Crea todas las tablas del subsistema
    public void crearTablas() throws SQLException {
        crearTablaLibro();
        crearTablaEjemplar();
        crearTablaIncidenciaEjemplar();
    }

    // Crea la tabla LIBRO
    private void crearTablaLibro() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS LIBRO (
                ISBN VARCHAR(13) PRIMARY KEY,
                Autor VARCHAR(20) NOT NULL,
                Titulo VARCHAR(150) NOT NULL,
                Editorial VARCHAR(20),
                FechaPublicacion DATE NOT NULL,
                NumPaginas INT NOT NULL,
                Edicion INT NOT NULL,
                Genero VARCHAR(50)
            )
        """;

        Statement st = conexion.createStatement();
        st.executeUpdate(sql);
        st.close();
    }

    // Crea la tabla EJEMPLAR
    private void crearTablaEjemplar() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS EJEMPLAR (
                ISBN VARCHAR(13),
                CodEjemplar INT,
                Estado VARCHAR(20) NOT NULL,
                PRIMARY KEY (ISBN, CodEjemplar),
                FOREIGN KEY (ISBN) REFERENCES LIBRO(ISBN)
            )
        """;

        Statement st = conexion.createStatement();
        st.executeUpdate(sql);
        st.close();
    }

    // Crea la tabla INCIDENCIA_EJEMPLAR
    private void crearTablaIncidenciaEjemplar() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS INCIDENCIA_EJEMPLAR (
                IDIncidencia INT PRIMARY KEY,
                ISBN VARCHAR(13),
                CodEjemplar INT,
                FechaRegistro DATE NOT NULL,
                Descripcion VARCHAR(500) NOT NULL,
                Prioridad INT,
                FechaResolucion DATE,
                FOREIGN KEY (ISBN, CodEjemplar)
                    REFERENCES EJEMPLAR(ISBN, CodEjemplar)
            )
        """;

        Statement st = conexion.createStatement();
        st.executeUpdate(sql);
        st.close();
    }
}
