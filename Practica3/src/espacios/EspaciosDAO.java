/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package espacios;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author uSer
 */
public class EspaciosDAO {
    private final Connection connection;

    public EspaciosDAO(Connection connection) {
        this.connection = connection;
    }
    
    /* =====================================================
       1. ALTA ESPACIO
       ===================================================== */
    public void insertarEspacio(Espacio e) throws SQLException {
        String sql =
            "INSERT INTO ESPACIOS (ESPACIO_ID, NOMBRE, TIPO, CAPACIDAD, ESTADO) "
          + "VALUES (SEQ_ESPACIOS.NEXTVAL, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getTipo());
            ps.setInt(3, e.getCapacidad());
            ps.setString(4, e.getEstado());

            ps.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
    
    /* =====================================================
       2. LISTAR ESPACIOS (RF-4.1)
       ===================================================== */
    public List<Espacio> listarEspacios(String estado, String tipo, Integer capacidadMin)
            throws SQLException {

        List<Espacio> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT ESPACIO_ID, NOMBRE, TIPO, CAPACIDAD, ESTADO FROM ESPACIOS WHERE 1=1");

        if (estado != null) sql.append(" AND ESTADO = ?");
        if (tipo != null) sql.append(" AND TIPO = ?");
        if (capacidadMin != null) sql.append(" AND CAPACIDAD >= ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            int i = 1;
            if (estado != null) ps.setString(i++, estado);
            if (tipo != null) ps.setString(i++, tipo);
            if (capacidadMin != null) ps.setInt(i++, capacidadMin);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Espacio(
                        rs.getInt("ESPACIO_ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("TIPO"),
                        rs.getInt("CAPACIDAD"),
                        rs.getString("ESTADO")
                ));
            }
        }
        return lista;
    }
    
    /* =====================================================
       3. NUEVA RESERVA (RF-4.2)
       ===================================================== */
     public int crearReserva(int usuarioId, int espacioId, Timestamp inicio, Timestamp fin) throws SQLException {

        String sqlNextId = "SELECT SEQ_RESERVAS_ESPACIOS.NEXTVAL FROM DUAL";
        String sqlInsert =
            "INSERT INTO RESERVAS_ESPACIOS "
          + "(RESERVA_ID, USUARIO_ID, ESPACIO_ID, INICIO, FIN, ESTADO_RESERVA) "
          + "VALUES (?, ?, ?, ?, ?, 'CONFIRMADA')";

        try {
            int reservaId;

            // 1) Obtener el ID
            try (PreparedStatement psId = connection.prepareStatement(sqlNextId);
                 ResultSet rs = psId.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException("No se pudo obtener SEQ_RESERVAS_ESPACIOS.NEXTVAL");
                }
                reservaId = rs.getInt(1);
            }

            // 2) Insertar con el ID
            try (PreparedStatement psIns = connection.prepareStatement(sqlInsert)) {
                psIns.setInt(1, reservaId);
                psIns.setInt(2, usuarioId);
                psIns.setInt(3, espacioId);
                psIns.setTimestamp(4, inicio);
                psIns.setTimestamp(5, fin);
                psIns.executeUpdate();
            }

            connection.commit();
            return reservaId;

        } catch (SQLException ex) {
            connection.rollback();
            throw ex; // aquí te puede llegar lo del trigger (solape, bloqueado, etc.)
        }
    }
    
     /* =====================================================
       4. CANCELAR RESERVA (RF-4.3)
       ===================================================== */
    public void cancelarReservaPorId(int reservaId) throws SQLException {

        // comprobar que existe y está confirmada, y que no ha empezado
        String check =
            "SELECT INICIO FROM RESERVAS_ESPACIOS "
          + "WHERE RESERVA_ID = ? AND ESTADO_RESERVA = 'CONFIRMADA'";

        Timestamp inicio;
        try (PreparedStatement psCheck = connection.prepareStatement(check)) {
            psCheck.setInt(1, reservaId);
            ResultSet rs = psCheck.executeQuery();
            if (!rs.next()) {
                throw new SQLException("No existe una reserva confirmada con ese ID");
            }
            inicio = rs.getTimestamp("INICIO");
        }

        if (inicio.before(new Timestamp(System.currentTimeMillis()))) {
            throw new SQLException("No se puede cancelar una reserva ya iniciada");
        }

        String sql =
            "UPDATE RESERVAS_ESPACIOS SET ESTADO_RESERVA = 'CANCELADA' "
          + "WHERE RESERVA_ID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservaId);
            ps.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
    
    
    /* =====================================================
       5. REGISTRAR INCIDENCIA (RF-4.4)
       ===================================================== */
    public void registrarIncidencia(IncidenciaEspacio inc) throws SQLException {
        try {
            if ("CANCELAR_Y_AVISAR".equalsIgnoreCase(inc.getAccionReserva())) {
                String cancelarReservas =
                    "UPDATE RESERVAS_ESPACIOS "
                  + "SET ESTADO_RESERVA = 'CANCELADA_INCIDENCIA' "
                  + "WHERE ESPACIO_ID = ? "
                  + "AND ESTADO_RESERVA = 'CONFIRMADA' "
                  + "AND NOT (FIN <= ? OR INICIO >= ?)";

                try (PreparedStatement ps = connection.prepareStatement(cancelarReservas)) {
                    ps.setInt(1, inc.getEspacioId());
                    ps.setTimestamp(2, inc.getInicioIncidencia());
                    ps.setTimestamp(3, inc.getFinIncidencia());
                    ps.executeUpdate();
                }
            }

            String insertarInc =
                "INSERT INTO INCIDENCIAS_ESPACIOS "
              + "(INCIDENCIA_ID, ESPACIO_ID, DESCRIPCION, INICIO_INCIDENCIA, FIN_INCIDENCIA, ACCION_RESERVA) "
              + "VALUES (SEQ_INCIDENCIAS_ESPACIOS.NEXTVAL, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps = connection.prepareStatement(insertarInc)) {
                ps.setInt(1, inc.getEspacioId());
                ps.setString(2, inc.getDescripcion());
                ps.setTimestamp(3, inc.getInicioIncidencia());
                ps.setTimestamp(4, inc.getFinIncidencia());
                ps.setString(5, inc.getAccionReserva());
                ps.executeUpdate();
            }

            String bloquear =
                "UPDATE ESPACIOS SET ESTADO = 'BLOQUEADO' WHERE ESPACIO_ID = ?";

            try (PreparedStatement ps = connection.prepareStatement(bloquear)) {
                ps.setInt(1, inc.getEspacioId());
                ps.executeUpdate();
            }

            connection.commit();

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
    
    /* =====================================================
       6. LISTAR RESERVAS (RF-4.5)
       ===================================================== */
    public List<ReservaEspacio> listarReservas(Integer espacioId, Timestamp inicio, Timestamp fin, String estado)
            throws SQLException {

        List<ReservaEspacio> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT RESERVA_ID, USUARIO_ID, ESPACIO_ID, INICIO, FIN, ESTADO_RESERVA "
          + "FROM RESERVAS_ESPACIOS WHERE 1=1");

        if (espacioId != null) sql.append(" AND ESPACIO_ID = ?");
        if (inicio != null) sql.append(" AND INICIO >= ?");
        if (fin != null) sql.append(" AND FIN <= ?");
        if (estado != null) sql.append(" AND ESTADO_RESERVA = ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int i = 1;
            if (espacioId != null) ps.setInt(i++, espacioId);
            if (inicio != null) ps.setTimestamp(i++, inicio);
            if (fin != null) ps.setTimestamp(i++, fin);
            if (estado != null) ps.setString(i++, estado);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new ReservaEspacio(
                        rs.getInt("RESERVA_ID"),
                        rs.getInt("USUARIO_ID"),
                        rs.getInt("ESPACIO_ID"),
                        rs.getTimestamp("INICIO"),
                        rs.getTimestamp("FIN"),
                        rs.getString("ESTADO_RESERVA")
                ));
            }
        }
        return lista;
    }
    
    
    
}
