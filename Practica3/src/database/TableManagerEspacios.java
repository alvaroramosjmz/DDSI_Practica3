package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TableManagerEspacios {

    private Connection connection;

    public TableManagerEspacios(Connection connection) {
        this.connection = connection;
    }

    public void crearEstructuraEspacios() throws SQLException {
        Statement stmt = connection.createStatement();

        // 1) Borrar elementos previos si existen (orden: triggers -> tablas -> secuencias)
        // ============ DROPS ============
        try { stmt.execute("DROP TRIGGER TRG_RESERVAS_SOLAPES"); } catch (SQLException e) {}
        try { stmt.execute("DROP TRIGGER TRG_RESERVAS_FECHAS"); } catch (SQLException e) {}
        try { stmt.execute("DROP TRIGGER TRG_ESPACIOS_MAYUSCULAS"); } catch (SQLException e) {}
        try { stmt.execute("DROP TRIGGER TRG_INCIDENCIAS_VALIDAR"); } catch (SQLException e) {}

        try { stmt.execute("DROP TABLE INCIDENCIAS_ESPACIOS CASCADE CONSTRAINTS"); } catch (SQLException e) {}
        try { stmt.execute("DROP TABLE RESERVAS_ESPACIOS CASCADE CONSTRAINTS"); } catch (SQLException e) {}
        try { stmt.execute("DROP TABLE ESPACIOS CASCADE CONSTRAINTS"); } catch (SQLException e) {}

        try { stmt.execute("DROP SEQUENCE SEQ_INCIDENCIAS_ESPACIOS"); } catch (SQLException e) {}
        try { stmt.execute("DROP SEQUENCE SEQ_RESERVAS_ESPACIOS"); } catch (SQLException e) {}
        try { stmt.execute("DROP SEQUENCE SEQ_ESPACIOS"); } catch (SQLException e) {}
        
         // 2) Crear tabla ESPACIOS
        String sqlEspacios =
            "CREATE TABLE ESPACIOS ("
          + "  ESPACIO_ID NUMBER PRIMARY KEY, "
          + "  NOMBRE VARCHAR2(80) NOT NULL, "
          + "  TIPO VARCHAR2(30) NOT NULL, "
          + "  CAPACIDAD NUMBER DEFAULT 0 NOT NULL CHECK (CAPACIDAD >= 0), "
          + "  ESTADO VARCHAR2(20) DEFAULT 'NO BLOQUEADO' NOT NULL "
          + "    CHECK (ESTADO IN ('BLOQUEADO', 'NO BLOQUEADO'))"
          + ")";
        stmt.execute(sqlEspacios);

        // 5) Crear tabla RESERVAS_ESPACIOS (CON RESERVA_ID)
        stmt.execute(
            "CREATE TABLE RESERVAS_ESPACIOS ("
          + "  RESERVA_ID NUMBER PRIMARY KEY, "
          + "  USUARIO_ID NUMBER NOT NULL, "
          + "  ESPACIO_ID NUMBER NOT NULL, "
          + "  INICIO TIMESTAMP NOT NULL, "
          + "  FIN TIMESTAMP NOT NULL, "
          + "  ESTADO_RESERVA VARCHAR2(30) DEFAULT 'CONFIRMADA' NOT NULL "
          + "    CHECK (ESTADO_RESERVA IN ('CONFIRMADA', 'CANCELADA', 'CANCELADA_INCIDENCIA')), "
          + "  CONSTRAINT FK_RESERVA_LECTOR FOREIGN KEY (USUARIO_ID) REFERENCES LECTORES(USUARIO_ID), "
          + "  CONSTRAINT FK_RESERVA_ESPACIO FOREIGN KEY (ESPACIO_ID) REFERENCES ESPACIOS(ESPACIO_ID), "
          + "  CONSTRAINT UQ_RESERVA_INTERVALO UNIQUE (ESPACIO_ID, INICIO, FIN) "
          + ")"
        );

        // 4) Crear tabla INCIDENCIAS_ESPACIOS
        String sqlIncidencias =
            "CREATE TABLE INCIDENCIAS_ESPACIOS ("
          + "  INCIDENCIA_ID NUMBER PRIMARY KEY, "
          + "  ESPACIO_ID NUMBER NOT NULL, "
          + "  DESCRIPCION VARCHAR2(500) NOT NULL, "
          + "  INICIO_INCIDENCIA TIMESTAMP NOT NULL, "
          + "  FIN_INCIDENCIA TIMESTAMP NOT NULL, "
          + "  ACCION_RESERVA VARCHAR2(30) DEFAULT 'MANTENER' NOT NULL "
          + "    CHECK (ACCION_RESERVA IN ('MANTENER', 'CANCELAR_Y_AVISAR')), "
          + "  CONSTRAINT FK_INCIDENCIA_ESPACIO FOREIGN KEY (ESPACIO_ID) REFERENCES ESPACIOS(ESPACIO_ID) "
          + ")";
        stmt.execute(sqlIncidencias);

        // 5) Crear secuencias
        stmt.execute("CREATE SEQUENCE SEQ_ESPACIOS START WITH 1 INCREMENT BY 1");
        stmt.execute("CREATE SEQUENCE SEQ_RESERVAS_ESPACIOS START WITH 1 INCREMENT BY 1");
        stmt.execute("CREATE SEQUENCE SEQ_INCIDENCIAS_ESPACIOS START WITH 1 INCREMENT BY 1");
        
        // 6) Triggers

        // 6.1) Normalización a mayúsculas
        String trgEspaciosMayus =
            "CREATE OR REPLACE TRIGGER TRG_ESPACIOS_MAYUSCULAS "
          + "BEFORE INSERT OR UPDATE ON ESPACIOS "
          + "FOR EACH ROW "
          + "BEGIN "
          + "  :NEW.NOMBRE := UPPER(:NEW.NOMBRE); "
          + "  :NEW.TIPO := UPPER(:NEW.TIPO); "
          + "  :NEW.ESTADO := UPPER(:NEW.ESTADO); "
          + "END;";
        stmt.execute(trgEspaciosMayus);
        
        // 4.2) Validación de FECHAS en reservas (NO toca ESTADO_RESERVA, así que no afecta a cancelar)
        String trgReservasFechas =
            "CREATE OR REPLACE TRIGGER TRG_RESERVAS_FECHAS "
          + "BEFORE INSERT OR UPDATE OF INICIO, FIN ON RESERVAS_ESPACIOS "
          + "FOR EACH ROW "
          + "BEGIN "
          + "  IF :NEW.FIN <= :NEW.INICIO THEN "
          + "    RAISE_APPLICATION_ERROR(-20010, 'La fecha FIN debe ser posterior a INICIO'); "
          + "  END IF; "
          + "END;";
        stmt.execute(trgReservasFechas);

        // 4.3) Solapes + espacio no bloqueado (SOLO INSERT o cambios de INICIO/FIN/ESPACIO_ID)
        //      -> Cancelar (UPDATE solo ESTADO_RESERVA) NO dispara este trigger => evita ORA-04091
        String trgReservasSolapes =
            "CREATE OR REPLACE TRIGGER TRG_RESERVAS_SOLAPES "
          + "BEFORE INSERT OR UPDATE OF INICIO, FIN, ESPACIO_ID ON RESERVAS_ESPACIOS "
          + "FOR EACH ROW "
          + "DECLARE "
          + "  v_estado  VARCHAR2(20); "
          + "  v_solapes NUMBER; "
          + "BEGIN "
          + "  IF :NEW.ESTADO_RESERVA <> 'CONFIRMADA' THEN "
          + "    RETURN; "
          + "  END IF; "
          + "  SELECT ESTADO INTO v_estado FROM ESPACIOS WHERE ESPACIO_ID = :NEW.ESPACIO_ID; "
          + "  IF v_estado = 'BLOQUEADO' THEN "
          + "    RAISE_APPLICATION_ERROR(-20011, 'No se puede reservar: el espacio está BLOQUEADO'); "
          + "  END IF; "
          + "  IF INSERTING THEN "
          + "    SELECT COUNT(*) INTO v_solapes "
          + "    FROM RESERVAS_ESPACIOS "
          + "    WHERE ESPACIO_ID = :NEW.ESPACIO_ID "
          + "      AND ESTADO_RESERVA = 'CONFIRMADA' "
          + "      AND NOT (FIN <= :NEW.INICIO OR INICIO >= :NEW.FIN); "
          + "  ELSE "
          + "    SELECT COUNT(*) INTO v_solapes "
          + "    FROM RESERVAS_ESPACIOS "
          + "    WHERE ESPACIO_ID = :NEW.ESPACIO_ID "
          + "      AND ESTADO_RESERVA = 'CONFIRMADA' "
          + "      AND NOT (FIN <= :NEW.INICIO OR INICIO >= :NEW.FIN) "
          + "      AND RESERVA_ID <> :NEW.RESERVA_ID; "
          + "  END IF; "
          + "  IF v_solapes > 0 THEN "
          + "    RAISE_APPLICATION_ERROR(-20012, 'No se puede reservar: existe solape con otra reserva'); "
          + "  END IF; "
          + "END;";
        stmt.execute(trgReservasSolapes);
        

        // 6.3) Validación básica de incidencias (FIN > INICIO)
        String trgIncidenciasValidar =
            "CREATE OR REPLACE TRIGGER TRG_INCIDENCIAS_VALIDAR "
          + "BEFORE INSERT OR UPDATE ON INCIDENCIAS_ESPACIOS "
          + "FOR EACH ROW "
          + "BEGIN "
          + "  IF :NEW.FIN_INCIDENCIA <= :NEW.INICIO_INCIDENCIA THEN "
          + "    RAISE_APPLICATION_ERROR(-20020, 'La fecha FIN_INCIDENCIA debe ser posterior a INICIO_INCIDENCIA'); "
          + "  END IF; "
          + "END;";
        stmt.execute(trgIncidenciasValidar);

        stmt.close();
    }
}