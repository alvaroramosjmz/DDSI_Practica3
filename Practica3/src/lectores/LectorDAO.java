/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lectores;

import java.sql.*;
import java.util.ArrayList;

public class LectorDAO {
    private Connection connection;

    public LectorDAO(Connection connection) {
        this.connection = connection;
    }

    // REQUISITO 2.1: Alta de Lector
    public void insertarLector(Lector l) throws SQLException {
        String sql = "INSERT INTO LECTORES (USUARIO_ID, NOMBRE, APELLIDOS, TELEFONO, ES_MALICIOSO) "
                   + "VALUES (SEQ_LECTORES.NEXTVAL, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, l.getNombre());
            pstmt.setString(2, l.getApellidos());
            pstmt.setString(3, l.getTelefono());
            pstmt.setString(4, l.getEsMalicioso());
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // REQUISITO 2.2: Modificación de Lector
    public void modificarLector(Lector l) throws SQLException {
        String sql = "UPDATE LECTORES SET NOMBRE = ?, APELLIDOS = ?, TELEFONO = ?, ES_MALICIOSO = ? "
                   + "WHERE USUARIO_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, l.getNombre());
            pstmt.setString(2, l.getApellidos());
            pstmt.setString(3, l.getTelefono());
            pstmt.setString(4, l.getEsMalicioso());
            pstmt.setInt(5, l.getId());
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // REQUISITO 2.3: Baja de Lector
    public void eliminarLector(int id) throws SQLException {
        String sql = "DELETE FROM LECTORES WHERE USUARIO_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // REQUISITO 2.4: Consulta de Lector por ID
    public Lector buscarLectorPorId(int id) throws SQLException {
        String sql = "SELECT * FROM LECTORES WHERE USUARIO_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Lector(
                        rs.getInt("USUARIO_ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("APELLIDOS"),
                        rs.getString("TELEFONO"),
                        rs.getString("ES_MALICIOSO")
                    );
                }
            }
        }
        return null; // Si no lo encuentra
    }

    // REQUISITO 2.5: Listado General
    public ArrayList<Lector> listarLectores() throws SQLException {
        ArrayList<Lector> lista = new ArrayList<>();
        String sql = "SELECT * FROM LECTORES ORDER BY USUARIO_ID";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Lector(
                    rs.getInt("USUARIO_ID"),
                    rs.getString("NOMBRE"),
                    rs.getString("APELLIDOS"),
                    rs.getString("TELEFONO"),
                    rs.getString("ES_MALICIOSO")
                ));
            }
        }
        return lista;
    }
}
