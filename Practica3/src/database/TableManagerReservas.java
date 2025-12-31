/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * TableManager para el Subsistema 3: Reserva de Libros
 * Se encarga de crear la tabla RESERVAS_LIBROS y sus restricciones.
 */
public class TableManagerReservas {

    private Connection connection;

    public TableManagerReservas(Connection connection) {
        this.connection = connection;
    }

    public void crearEstructuraReservas() throws SQLException {
        Statement stmt = connection.createStatement();

        // ==========================================
        // 1. FASE DE LIMPIEZA (DROPS)
        // Borramos en orden inverso a la creación
        // ==========================================
        try { stmt.execute("DROP TRIGGER TRG_RESERVAS_ID"); } catch (SQLException e) {}
        try { stmt.execute("DROP TABLE RESERVAS_LIBROS CASCADE CONSTRAINTS"); } catch (SQLException e) {}
        try { stmt.execute("DROP SEQUENCE SEQ_RESERVAS_LIBROS"); } catch (SQLException e) {}

        // ==========================================
        // 2. CREACIÓN DE LA TABLA
        // ==========================================
        // - LECTORES (Gestionada por TableManagerLectores)
        // - EJEMPLAR (Gestionada por TableManagerLibros)
        String sqlTabla = "CREATE TABLE RESERVAS_LIBROS ("
                + "  RESERVA_ID NUMBER PRIMARY KEY, "
                + "  ISBN VARCHAR2(13) NOT NULL, "
                + "  COD_EJEMPLAR NUMBER NOT NULL, "
                + "  USUARIO_ID NUMBER NOT NULL, "
                + "  FECHA_RESERVA DATE DEFAULT SYSDATE NOT NULL, "
                + "  RESERVA_VALIDA CHAR(1) DEFAULT 'T' CHECK (RESERVA_VALIDA IN ('T', 'F')), "
                
                //Para comprobar si se ha devuelto el libro o no
                + "  ES_RETIRADO CHAR(1) DEFAULT 'N' CHECK (ES_RETIRADO IN ('S', 'N')), "
                
                // Clave Foránea 1: Un usuario debe existir en la tabla de Lectores
                + "  CONSTRAINT FK_RESERVAS_LECTOR FOREIGN KEY (USUARIO_ID) "
                + "    REFERENCES LECTORES(USUARIO_ID) ON DELETE CASCADE, " //si un usuario se da de baja , se borran autmaticamente sus reservas

                // Clave Foránea 2: El ejemplar debe existir en la tabla de Libros/Ejemplares
                // OJO: TableManagerLibros define la PK de Ejemplar como (ISBN, CodEjemplar)
                + "  CONSTRAINT FK_RESERVAS_EJEMPLAR FOREIGN KEY (ISBN, COD_EJEMPLAR) "
                + "    REFERENCES EJEMPLAR(ISBN, CodEjemplar) ON DELETE CASCADE "
                + ")";
        
        stmt.execute(sqlTabla);
        System.out.println(">> Tabla RESERVAS_LIBROS creada.");

        // ==========================================
        // 3. CREACIÓN DE SECUENCIA
        // ==========================================
        // Para autogenerar el ID de la reserva (1, 2, 3...)
        String sqlSeq = "CREATE SEQUENCE SEQ_RESERVAS_LIBROS START WITH 1 INCREMENT BY 1";
        stmt.execute(sqlSeq);
        System.out.println(">> Secuencia SEQ_RESERVAS_LIBROS creada.");

        // ==========================================
        // 4. CREACIÓN DE TRIGGER (PL/SQL)
        // ==========================================
        // Requisito Práctica 3: Trigger para asignar ID automáticamente y validar fecha.
        String sqlTrigger = "CREATE OR REPLACE TRIGGER TRG_RESERVAS_ID "
                + "BEFORE INSERT ON RESERVAS_LIBROS "
                + "FOR EACH ROW "
                + "BEGIN "
                // Si no viene ID, usamos la secuencia
                + "  IF :NEW.RESERVA_ID IS NULL THEN "
                + "    SELECT SEQ_RESERVAS_LIBROS.NEXTVAL INTO :NEW.RESERVA_ID FROM DUAL; "
                + "  END IF; "
                // Aseguramos que la fecha sea la actual si viene vacía
                + "  IF :NEW.FECHA_RESERVA IS NULL THEN "
                + "    :NEW.FECHA_RESERVA := SYSDATE; "
                + "  END IF; "
                + "END;";
        
        stmt.execute(sqlTrigger);
        System.out.println(">> Trigger TRG_RESERVAS_ID creado.");
    }
}
