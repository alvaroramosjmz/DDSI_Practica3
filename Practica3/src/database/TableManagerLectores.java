package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableManagerLectores {

    private Connection connection;

    public TableManagerLectores(Connection connection) {
        this.connection = connection;
    }

    public void crearEstructuraLectores() throws SQLException {
        Statement stmt = connection.createStatement();

        // 1. Borrar elementos previos si existen
        try { stmt.execute("DROP TRIGGER TRG_LECTORES_MAYUSCULAS"); } catch (SQLException e) {}
        try { stmt.execute("DROP TABLE LECTORES CASCADE CONSTRAINTS"); } catch (SQLException e) {}
        try { stmt.execute("DROP SEQUENCE SEQ_LECTORES"); } catch (SQLException e) {}

        // 2. Crear Tabla LECTORES
        String sqlTabla = "CREATE TABLE LECTORES ("
                + "USUARIO_ID NUMBER PRIMARY KEY, "
                + "NOMBRE VARCHAR2(50) NOT NULL, "
                + "APELLIDOS VARCHAR2(50), "
                + "TELEFONO VARCHAR2(20) UNIQUE, "
                + "ES_MALICIOSO CHAR(1) DEFAULT 'N' CHECK (ES_MALICIOSO IN ('S', 'N'))"
                + ")";
        stmt.execute(sqlTabla);

        // 3. Crear Secuencia
        stmt.execute("CREATE SEQUENCE SEQ_LECTORES START WITH 1 INCREMENT BY 1");

        // 4. Crear Trigger
        String sqlTrigger = "CREATE OR REPLACE TRIGGER TRG_LECTORES_MAYUSCULAS "
                + "BEFORE INSERT OR UPDATE ON LECTORES "
                + "FOR EACH ROW "
                + "BEGIN "
                + "  :NEW.NOMBRE := UPPER(:NEW.NOMBRE); "
                + "  :NEW.APELLIDOS := UPPER(:NEW.APELLIDOS); "
                + "END;";
        stmt.execute(sqlTrigger);

        stmt.close();
    }
}