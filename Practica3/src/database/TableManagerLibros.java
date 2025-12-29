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

         // Borra triggers
        try { st.execute("DROP TRIGGER TR_EJEMPLAR_ESTADO_DEFECTO"); } catch (SQLException e) {}
        try { st.execute("DROP TRIGGER TR_EJEMPLAR_CODE_AUTO"); } catch (SQLException e) {}
        try { st.execute("DROP TRIGGER TR_INCIDENCIA_ID_AUTO"); } catch (SQLException e) {}
        
        // Borra secuencia
        try { st.execute("DROP SEQUENCE SEQ_INCIDENCIA"); } catch (SQLException e) {}

        // Borra tablas
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
                Genero VARCHAR2(50)
            )
        """);

        // Crea la tabla EJEMPLAR
        st.execute("""
            CREATE TABLE EJEMPLAR (
                ISBN VARCHAR2(13),
                CodEjemplar NUMBER,
                Estado VARCHAR2(20),
                PRIMARY KEY (ISBN, CodEjemplar),
                FOREIGN KEY (ISBN) REFERENCES LIBRO(ISBN),
                 CHECK (Estado IN ('DISPONIBLE', 'NO_DISPONIBLE', 'DESCATALOGADO'))
            )
        """);

        // Crea la tabla INCIDENCIA_EJEMPLAR
        st.execute("""
            CREATE TABLE INCIDENCIA_EJEMPLAR (
                IDIncidencia NUMBER PRIMARY KEY,
                ISBN VARCHAR2(13),
                CodEjemplar NUMBER,
                FechaRegistro DATE ,
                Descripcion VARCHAR2(500),
                Prioridad NUMBER,
                FechaResolucion DATE,
                FOREIGN KEY (ISBN, CodEjemplar)
                    REFERENCES EJEMPLAR(ISBN, CodEjemplar)
            )
        """);
        
        // Creo secuencia para los idIncidencia
        st.execute("""
            CREATE SEQUENCE SEQ_INCIDENCIA
            START WITH 1
            INCREMENT BY 1
            NOCACHE
        """);
        
        // TRIGGERS
    
        // Estado 'DISPONIBLE' por defecto del ejemplar
        st.execute("""
            CREATE OR REPLACE TRIGGER TR_EJEMPLAR_ESTADO_DEFECTO
            BEFORE INSERT ON EJEMPLAR
            FOR EACH ROW
            BEGIN
                :NEW.Estado := 'DISPONIBLE';
            END;
        """);
       
         // Generacion automatica de CodEjemplar por ISBN
        st.execute("""
            CREATE OR REPLACE TRIGGER TR_EJEMPLAR_CODE_AUTO
            BEFORE INSERT ON EJEMPLAR
            FOR EACH ROW
            DECLARE
                v_max NUMBER;
            BEGIN
                SELECT NVL(MAX(CodEjemplar), 0) + 1
                INTO v_max
                FROM EJEMPLAR
                WHERE ISBN = :NEW.ISBN;

                :NEW.CodEjemplar := v_max;
            END;
        """);
        
        // Generacion automatica de IDIncidencia y FechaRegistro
        st.execute("""
            CREATE OR REPLACE TRIGGER TR_INCIDENCIA_AUTO
            BEFORE INSERT ON INCIDENCIA_EJEMPLAR
            FOR EACH ROW
            BEGIN
                :NEW.IDIncidencia   := SEQ_INCIDENCIA.NEXTVAL;
                :NEW.FechaRegistro := SYSDATE;
            END;
        """);


        st.close();
        conexion.commit();
    }
}
