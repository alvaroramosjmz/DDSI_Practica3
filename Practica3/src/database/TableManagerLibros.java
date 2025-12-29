package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * TableManager del subsistema de Control de Libros
 * 
 * Crea:
 *  - LIBRO
 *  - EJEMPLAR
 *  - INCIDENCIA_EJEMPLAR
 * Incluye restricciones y triggers exigidos en la práctica.
 */
public class TableManagerLibros {

    private Connection conexion;

    public TableManagerLibros(Connection conexion) {
        this.conexion = conexion;
    }

    public void crearEstructuraLibros() throws SQLException {

        Statement st = conexion.createStatement();

        // Borra todas las tablas y restricciones que pudiese haber ya creadas
        try { st.execute("DROP TRIGGER TRG_INC_ESTADO"); } catch (SQLException e) {}
        try { st.execute("DROP TRIGGER TRG_INC_VALIDAR"); } catch (SQLException e) {}
        try { st.execute("DROP TABLE INCIDENCIA_EJEMPLAR CASCADE CONSTRAINTS"); } catch (SQLException e) {}
        try { st.execute("DROP TABLE EJEMPLAR CASCADE CONSTRAINTS"); } catch (SQLException e) {}
        try { st.execute("DROP TABLE LIBRO CASCADE CONSTRAINTS"); } catch (SQLException e) {}

        // Crea tabla LIBRO
        st.execute("""
            CREATE TABLE LIBRO (
                ISBN VARCHAR2(13) PRIMARY KEY,
                Autor VARCHAR2(20) NOT NULL,
                Titulo VARCHAR2(150) NOT NULL,
                Editorial VARCHAR2(20),
                FechaPublicacion DATE,
                NumPaginas NUMBER,
                Edicion NUMBER,
                Genero VARCHAR2(50),
                CHECK (FechaPublicacion IS NULL OR FechaPublicacion <= SYSDATE)
            )
        """);

        System.out.println(">> Tabla LIBRO creada.");

        // Crea la tabla EJEMPLAR
        st.execute("""
            CREATE TABLE EJEMPLAR (
                ISBN VARCHAR2(13),
                CodEjemplar NUMBER,
                Estado VARCHAR2(20) NOT NULL,
                PRIMARY KEY (ISBN, CodEjemplar),
                FOREIGN KEY (ISBN) REFERENCES LIBRO(ISBN),
                CHECK (Estado IN ('DISPONIBLE','NO_DISPONIBLE','DESCATALOGADO'))
            )
        """);

        System.out.println(">> Tabla EJEMPLAR creada.");

        // Crea la tabla INCIDENCIA_EJEMPLAR
        st.execute("""
            CREATE TABLE INCIDENCIA_EJEMPLAR (
                IDIncidencia NUMBER PRIMARY KEY,
                ISBN VARCHAR2(13),
                CodEjemplar NUMBER,
                FechaRegistro DATE NOT NULL,
                Descripcion VARCHAR2(500) NOT NULL,
                Prioridad NUMBER,
                FechaResolucion DATE,
                FOREIGN KEY (ISBN, CodEjemplar)
                    REFERENCES EJEMPLAR(ISBN, CodEjemplar),
                CHECK (Prioridad IS NULL OR Prioridad BETWEEN 1 AND 5),
                CHECK (FechaResolucion IS NULL OR FechaResolucion >= FechaRegistro)
            )
        """);

        st.close();
        conexion.commit();
        System.out.println(">> Subsistema LIBROS inicializado correctamente.");
    }
}
