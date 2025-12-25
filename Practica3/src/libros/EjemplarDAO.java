/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package libros;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Usuario
 */
public class EjemplarDAO {
    
    private Connection conexion;

    public EjemplarDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // Genera un nuevo código de ejemplar para un libro (MAX + 1)
    public int generarNuevoCodigoEjemplar(String isbn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(CodEjemplar),0) + 1 FROM EJEMPLAR WHERE ISBN = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, isbn);

        ResultSet rs = ps.executeQuery();
        rs.next();
        int nuevoCodigo = rs.getInt(1);

        rs.close();
        ps.close();
        return nuevoCodigo;
    }

    // Inserta un nuevo ejemplar en estado DISPONIBLE
    public void insertarEjemplar(String isbn, int codEjemplar, EstadoEjemplar estado) throws SQLException {
        String sql = "INSERT INTO EJEMPLAR (ISBN, CodEjemplar, Estado) VALUES (?, ?, ?)";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, isbn);
        ps.setInt(2, codEjemplar);
        ps.setString(3, estado.name());

        ps.executeUpdate();
        ps.close();
    }

    // Obtiene un ejemplar disponible de un libro (el primero que encuentre)
    public Integer obtenerEjemplarDisponible(String isbn) throws SQLException {
        String sql = "SELECT CodEjemplar FROM EJEMPLAR WHERE ISBN = ? AND Estado = 'DISPONIBLE'";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, isbn);

        ResultSet rs = ps.executeQuery();
        Integer cod = null;
        if (rs.next()) {
            cod = rs.getInt("CodEjemplar");
        }

        rs.close();
        ps.close();
        return cod;
    }

    // Cambia el estado de un ejemplar (baja lógica)
    public void cambiarEstadoEjemplar(String isbn, int codEjemplar, EstadoEjemplar estado) throws SQLException {
        String sql = "UPDATE EJEMPLAR SET Estado = ? WHERE ISBN = ? AND CodEjemplar = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, estado.name());
        ps.setString(2, isbn);
        ps.setInt(3, codEjemplar);

        ps.executeUpdate();
        ps.close();
    }

    // Comprueba si existe un ejemplar concreto (ISBN + CodEjemplar)
    public boolean existeEjemplar(String isbn, int codEjemplar) throws SQLException {
        String sql = "SELECT COUNT(*) FROM EJEMPLAR WHERE ISBN = ? AND CodEjemplar = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, isbn);
        ps.setInt(2, codEjemplar);

        ResultSet rs = ps.executeQuery();
        rs.next();
        boolean existe = rs.getInt(1) > 0;

        rs.close();
        ps.close();
        return existe;
    }

    // Obtiene el estado actual de un ejemplar
    public EstadoEjemplar obtenerEstadoEjemplar(String isbn, int codEjemplar) throws SQLException {
        String sql = "SELECT Estado FROM EJEMPLAR WHERE ISBN = ? AND CodEjemplar = ?";
        PreparedStatement ps = conexion.prepareStatement(sql);
        ps.setString(1, isbn);
        ps.setInt(2, codEjemplar);

        ResultSet rs = ps.executeQuery();
        EstadoEjemplar estado = null;
        if (rs.next()) {
            estado = EstadoEjemplar.valueOf(rs.getString("Estado"));
        }

        rs.close();
        ps.close();
        return estado;
    }
}
