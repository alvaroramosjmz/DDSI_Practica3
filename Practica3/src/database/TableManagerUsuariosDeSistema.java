package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableManagerUsuariosDeSistema {
    private Connection connection;

    public TableManagerUsuariosDeSistema(Connection connection) {
        this.connection = connection;
    }

    public void crearEstructuraUsuarios() throws SQLException {
        Statement stmt = connection.createStatement();

        // ==========================================
        // 1. LIMPIEZA (DROPS)
        // ==========================================
        // Triggers
        try { stmt.execute("DROP TRIGGER TRG_USUARIOS_ID"); } catch (SQLException e) {}
        try { stmt.execute("DROP TRIGGER TRG_PROTEGER_HISTORIAL"); } catch (SQLException e) {}
        
        // Tablas
        try { stmt.execute("DROP TABLE ACCESOS_SISTEMA CASCADE CONSTRAINTS"); } catch (SQLException e) {}
        try { stmt.execute("DROP TABLE USUARIOS_SISTEMA CASCADE CONSTRAINTS"); } catch (SQLException e) {}
        
        // ==========================================
        // 2. TABLA USUARIOS_SISTEMA
        // ==========================================
        String sqlUsuarios = "CREATE TABLE USUARIOS_SISTEMA ("
                    + "  ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY , "
                    + "  NOMBRE VARCHAR2(60) NOT NULL, "
                    + "  EMAIL VARCHAR2(100) UNIQUE NOT NULL, "
                    + "  TELEFONO VARCHAR2(12) UNIQUE NOT NULL CONSTRAINT CHK_TLF_FORMAT CHECK (REGEXP_LIKE(TELEFONO, '^\\+[0-9]{11}$')), " 
                    + "  ES_ADMIN CHAR(1) DEFAULT 'N' CHECK (ES_ADMIN IN ('S', 'N')), "
                    + "  ES_MALICIOSO CHAR(1) DEFAULT 'N' CHECK (ES_MALICIOSO IN ('S', 'N')) "
                    + ")";
        stmt.execute(sqlUsuarios);

        // ==========================================
        // 3. TABLA ACCESOS_SISTEMA
        // ==========================================
        String sqlHistorial = "CREATE TABLE ACCESOS_SISTEMA ("
                    + "  ACCESO_ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                    + "  USUARIO_ID NUMBER NOT NULL, "
                    + "  FECHA_ACCESO TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "  CONSTRAINT FK_ACCESO_USER FOREIGN KEY (USUARIO_ID) REFERENCES USUARIOS_SISTEMA(ID)"
                    + ")";
        stmt.execute(sqlHistorial);

        // Trigger de Inmutabilidad del Historial (RS-5.3.2)
        String triggerHistorial = 
            "CREATE OR REPLACE TRIGGER TRG_PROTEGER_HISTORIAL " +
            "BEFORE DELETE OR UPDATE ON ACCESOS_SISTEMA " +
            "BEGIN " +
            "    RAISE_APPLICATION_ERROR(-20001, 'Violación de Seguridad (RS-5.3.2): El historial es inmutable.'); " +
            "END;";
        stmt.execute(triggerHistorial);

        // ==========================================
        // 4. DATOS DE PRUEBA
        // ==========================================
        stmt.execute("INSERT INTO USUARIOS_SISTEMA (NOMBRE, EMAIL, TELEFONO, ES_ADMIN, ES_MALICIOSO) "
                    + "VALUES ('Marta', 'e.mzladron72@go.ugr.es', '+34600000000', 'S', 'N')");
        stmt.execute("INSERT INTO USUARIOS_SISTEMA (NOMBRE, EMAIL, TELEFONO, ES_ADMIN, ES_MALICIOSO) "
                    + "VALUES ('Laura', 'e.lauragrciap@go.ugr.es', '+34600000001', 'S', 'N')");
        stmt.execute("INSERT INTO USUARIOS_SISTEMA (NOMBRE, EMAIL, TELEFONO, ES_ADMIN, ES_MALICIOSO) "
                    + "VALUES ('Mario', 'e.mario10@go.ugr.es', '+34600000002', 'S', 'N')");
        stmt.execute("INSERT INTO USUARIOS_SISTEMA (NOMBRE, EMAIL, TELEFONO, ES_ADMIN, ES_MALICIOSO) "
                    + "VALUES ('Fran', 'e.ingmatfco@go.ugr.es', '+34600000003', 'S', 'N')");
        stmt.execute("INSERT INTO USUARIOS_SISTEMA (NOMBRE, EMAIL, TELEFONO, ES_ADMIN, ES_MALICIOSO) "
                    + "VALUES ('Alvaro', 'e.alvaroramosjmz@go.ugr.es', '+34600000004', 'S', 'N')");
        
        stmt.execute("INSERT INTO USUARIOS_SISTEMA (NOMBRE, EMAIL, TELEFONO, ES_ADMIN, ES_MALICIOSO) "
                    + "VALUES ('Pepe Bibliotecario', 'biblio@ugr.es', '+34600000005', 'N', 'N')");

        stmt.close();
    }
}