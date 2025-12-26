package usuarioDeSistema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDeSistemaDAO {
    private Connection conn;

    public UsuarioDeSistemaDAO(Connection conn) {
        this.conn = conn;
    }

    public UsuarioDeSistema login(String email) throws SQLException {
        String sql = "SELECT ID, NOMBRE, EMAIL, TELEFONO, ES_ADMIN, ES_MALICIOSO FROM USUARIOS_SISTEMA WHERE EMAIL = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Mapear al nuevo constructor
                return new UsuarioDeSistema(
                    rs.getInt("ID"),
                    rs.getString("NOMBRE"),
                    rs.getString("EMAIL"),
                    rs.getString("TELEFONO"),
                    rs.getString("ES_ADMIN").equals("S"),
                    rs.getString("ES_MALICIOSO").equals("S")
                );
            }
        }
        return null; // Si no encuentra nada devuelve null
    }
}