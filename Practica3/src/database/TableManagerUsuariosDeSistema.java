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

        // 1. LIMPIEZA (Orden inverso por las Foreign Keys)
        try { stmt.execute("DROP TABLE ACCESOS_SISTEMA CASCADE CONSTRAINTS"); } catch (SQLException e) {}
        try { stmt.execute("DROP TABLE USUARIOS_SISTEMA CASCADE CONSTRAINTS"); } catch (SQLException e) {}

        // 2. TABLA USUARIOS_SISTEMA (Actualizada RF-5.1)
        String sqlUsuarios = "CREATE TABLE USUARIOS_SISTEMA ("
                   + "  ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                   + "  NOMBRE VARCHAR2(60) NOT NULL, "
                   + "  EMAIL VARCHAR2(100) UNIQUE NOT NULL, "
                   + "  TELEFONO VARCHAR2(12) UNIQUE NOT NULL, " // Formato +34...
                   + "  ES_ADMIN CHAR(1) DEFAULT 'N' CHECK (ES_ADMIN IN ('S', 'N')), "
                   + "  ES_MALICIOSO CHAR(1) DEFAULT 'N' CHECK (ES_MALICIOSO IN ('S', 'N')) "
                   + ")";
        stmt.execute(sqlUsuarios);

        // 3. TABLA ACCESOS_SISTEMA (Nueva RF-5.2)
        String sqlHistorial = "CREATE TABLE ACCESOS_SISTEMA ("
                   + "  ACCESO_ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                   + "  USUARIO_ID NUMBER NOT NULL, "
                   + "  FECHA_ACCESO TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                   + "  CONSTRAINT FK_ACCESO_USER FOREIGN KEY (USUARIO_ID) REFERENCES USUARIOS_SISTEMA(ID)"
                   + ")";
        stmt.execute(sqlHistorial);

        // 4. INSERTAR ADMIN INICIAL
        // Nota: Telefono ficticio que cumple formato
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
        
        // 5. INSERTAR BIBLIOTECARIO DE PRUEBA
        stmt.execute("INSERT INTO USUARIOS_SISTEMA (NOMBRE, EMAIL, TELEFONO, ES_ADMIN, ES_MALICIOSO) "
                   + "VALUES ('Pepe Bibliotecario', 'biblio@ugr.es', '+34600000005', 'N', 'N')");

        stmt.close();
        System.out.println("[INIT] Tablas de Seguridad y Usuarios creadas.");
    }
}